package io.pinect.azeron.server.decorator;

import io.pinect.azeron.server.domain.entity.MessageEntity;
import io.pinect.azeron.server.domain.repository.MessageRepository;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PreDestroy;
import java.util.*;

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
            messageEntity.getSeenSubscribers().add(serviceName);
            messageEntity.increaseSeenCount();
        }else{
            messageRepository.seenMessage(messageId, serviceName);
        }
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

            if(++matched == limit){
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
            unseenMessagesOfService = new MessageResult(results, true);
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
        if(cacheMap.size() > maximumCacheSize){
            long time = new Date().getTime();
            int i = secondsToConsiderUnAck * 1000;
            cacheMap.forEach((s, messageEntity) -> {
                if(time - messageEntity.getDate().getTime() > i){
                    try {
                        messageRepository.addMessage(messageEntity);
                    }catch (Exception e){
                        log.error("Failed to save message to main repository.", e);
                    }finally {
                        cacheMap.remove(s);
                    }
                }
            });
        }
    }

    private void addToCache(MessageEntity messageEntity) {
        cacheMap.putIfAbsent(messageEntity.getMessageId(), messageEntity);
    }
}
