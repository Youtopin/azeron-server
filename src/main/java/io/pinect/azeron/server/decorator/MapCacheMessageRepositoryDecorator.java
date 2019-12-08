package io.pinect.azeron.server.decorator;

import io.pinect.azeron.server.domain.entity.MessageEntity;
import io.pinect.azeron.server.domain.repository.MessageRepository;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class MapCacheMessageRepositoryDecorator extends MessageRepositoryDecorator {
    private final Map<String, MessageEntity> cacheMap;
    private final int maximumCacheSize;
    private final int secondsToConsiderUnAck;

    public MapCacheMessageRepositoryDecorator(MessageRepository messageRepository, Map<String, MessageEntity> cacheMap, int maximumCacheSize, int secondsToConsiderUnAck) {
        super(messageRepository);
        this.cacheMap = cacheMap;
        this.maximumCacheSize = maximumCacheSize;
        this.secondsToConsiderUnAck = secondsToConsiderUnAck;
    }

    @Override
    public MessageEntity addMessage(MessageEntity messageEntity) {
        log.debug("Adding message from decorator -> " + messageEntity.toString());

        addToCache(messageEntity);
        commitCacheIfNeeded();

        return messageEntity;
    }

    @Override
    public void seenMessage(String messageId, String serviceName) {
        MessageEntity messageEntity = cacheMap.get(messageId);
        if(messageEntity != null){
            if(messageEntity.getSeenSubscribers().contains(serviceName)){
                return;
            }
            messageEntity.getSeenSubscribers().add(serviceName);
            messageEntity.increaseSeenCount();
            if(messageEntity.getSeenCount() == messageEntity.getSeenNeeded()){
                removeMessage(messageId);
            }
        }else{
            makeTemporaryCache(messageId, serviceName);
            readFromDiskToCache(messageId);
            messageRepository.seenMessage(messageId, serviceName);
        }
    }

    private void makeTemporaryCache(String messageId, String serviceName) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setSeenSubscribers(getSet(serviceName));
        messageEntity.setSeenCount(1);
        messageEntity.setMessageId(messageId);
        messageEntity.setDate(new Date());
        messageEntity.setDirty(true);
        cacheMap.put(messageId, messageEntity);
    }

    public void readFromDiskToCache(String messageId){
        MessageEntity message = messageRepository.getMessage(messageId);
        List<String> newSubscribers = new ArrayList<>();
        if(message != null){
            MessageEntity cached = cacheMap.get(messageId);
            Set<String> subscribers = message.getSubscribers();
            AtomicInteger seenCount = new AtomicInteger(message.getSeenCount());
            cached.getSubscribers().forEach(subscriber -> {
                if (!subscribers.contains(subscriber)) {
                    subscribers.add(subscriber);
                    newSubscribers.add(subscriber);
                    seenCount.getAndIncrement();
                }
            });
            message.setSubscribers(subscribers);
            message.setSeenCount(seenCount.get());
            cacheMap.put(messageId, message);
        }

        newSubscribers.forEach(s -> {
            messageRepository.seenMessage(messageId, s);
        });
    }

    private Set<String> getSet(String s){
        Set<String> set = new HashSet<String>();
        set.add(s);
        return set;
    }

    @Override
    public void seenMessages(List<String> messageIds, String serviceName) {
        messageIds.forEach(s -> {
            seenMessage(s, serviceName);
        });
    }

    @Override
    public void removeMessage(String messageId) {
        cacheMap.remove(messageId);
        messageRepository.removeMessage(messageId);
    }


    //uses only limit
    @Override
    public MessageResult getUnseenMessagesOfService(String serviceName, int offset, int limit, Date before) {
        int matched = 0;
        List<MessageEntity> results = new ArrayList<>();
        boolean hasMore = false;
        for(String key: cacheMap.keySet()){
            MessageEntity messageEntity = cacheMap.get(key);
            if(messageEntity != null){
                if(messageEntity.getSubscribers().contains(serviceName) && messageEntity.getDate().before(before)){
                    results.add(messageEntity);
                }
            }
            matched++;
            if(matched == limit){
                hasMore = true;
                break;
            }
        }

        MessageResult unseenMessagesOfService;

        if(!hasMore){
            unseenMessagesOfService = messageRepository.getUnseenMessagesOfService(serviceName, offset, limit - results.size(), before);
            List<MessageEntity> messages = unseenMessagesOfService.getMessages();
            for(MessageEntity messageEntity: results){
                if(!results.contains(messageEntity)){
                    messages.add(messageEntity);
                }
            }
            unseenMessagesOfService.setMessages(messages);
        }else {
            unseenMessagesOfService = new MessageResult(results, results.size() == limit);
        }

        return unseenMessagesOfService;
    }

    @Override
    public MessageEntity getMessage(String messageId) {
        return cacheMap.getOrDefault(messageId, messageRepository.getMessage(messageId));
    }

    public void flush(){
        cacheMap.forEach((s, messageEntity) -> {
            messageRepository.addMessage(messageEntity);
        });
    }

    private void commitCacheIfNeeded() {
        removeDirties();

        long time = new Date().getTime();
        int i = secondsToConsiderUnAck * 1000;

        int stage = 1;

        while (cacheMap.size() > maximumCacheSize - 10){
            switch (stage){
                case 1:
                    cacheMap.forEach((s, messageEntity) -> {
                        if(time - messageEntity.getDate().getTime() > i){
                            commitMessage(messageEntity);
                        }
                    });

                    stage++;
                    break;
                case 2:
                    List<MessageEntity> messageEntities = getSortedMessages(cacheMap);
                    messageEntities.stream().skip(0).limit(cacheMap.size() - maximumCacheSize + 20).forEach(this::commitMessage);
                    stage = 1;
                    break;
            }

        }
    }

    private void removeDirties() {
        long time = new Date().getTime();
        cacheMap.forEach((key, messageEntity) -> {
            if(messageEntity.isDirty() && time - messageEntity.getDate().getTime() > 20000){
                cacheMap.remove(key);
            }
        });
    }

    private List<MessageEntity> getSortedMessages(Map<String, MessageEntity> cacheMap) {
        ArrayList<MessageEntity> sortedEntities = new ArrayList<>(cacheMap.values());
        Collections.sort(sortedEntities);
        return sortedEntities;
    }

    private void commitMessage(MessageEntity messageEntity){
        try {
            messageRepository.addMessage(messageEntity);
        }catch (Exception e){
            log.error("Failed to save message to main repository.", e);
        }finally {
            cacheMap.remove(messageEntity.getMessageId());
        }
    }

    private void addToCache(MessageEntity messageEntity) {
        MessageEntity alreadyCachedMessage = cacheMap.putIfAbsent(messageEntity.getMessageId(), messageEntity);
        if(alreadyCachedMessage != null){
            alreadyCachedMessage.setDirty(false);
        }
    }
}
