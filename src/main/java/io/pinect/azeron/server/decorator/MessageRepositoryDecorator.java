package io.pinect.azeron.server.decorator;

import io.pinect.azeron.server.domain.entity.MessageEntity;
import io.pinect.azeron.server.domain.repository.MessageRepository;

import java.util.Date;
import java.util.List;

public class MessageRepositoryDecorator implements MessageRepository {
    protected final MessageRepository messageRepository;

    public MessageRepositoryDecorator(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public MessageEntity addMessage(MessageEntity messageEntity) {
        return messageRepository.addMessage(messageEntity);
    }

    @Override
    public void seenMessage(String messageId, String serviceName) {
        messageRepository.seenMessage(messageId, serviceName);
    }

    @Override
    public void seenMessages(List<String> messageId, String serviceName) {
        messageRepository.seenMessages(messageId, serviceName);
    }

    @Override
    public void removeMessage(String messageId) {
        messageRepository.removeMessage(messageId);
    }

    @Override
    public MessageResult getUnseenMessagesOfService(String serviceName, int offset, int limit, Date before) {
        return messageRepository.getUnseenMessagesOfService(serviceName, offset, limit, before);
    }

    @Override
    public MessageEntity getMessage(String messageId) {
        return messageRepository.getMessage(messageId);
    }
}
