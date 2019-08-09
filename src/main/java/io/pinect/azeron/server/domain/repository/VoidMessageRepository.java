package io.pinect.azeron.server.domain.repository;

import io.pinect.azeron.server.domain.entity.MessageEntity;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class VoidMessageRepository implements MessageRepository {
    public VoidMessageRepository() {
        log.warn("Message repository is not overridden and default VoidMessageRepository is used.");
    }

    @Override
    public void addMessage(MessageEntity messageEntity) {

    }

    @Override
    public MessageEntity seenMessage(String messageId, String serviceName) {
        return new MessageEntity();
    }

    @Override
    public MessageEntity seenMessages(List<String> messageId, String serviceName) {
        return new MessageEntity();
    }

    @Override
    public MessageEntity removeMessage(String messageId) {
        return new MessageEntity();
    }

    @Override
    public MessageResult getUnseenMessagesOfService(String serviceName) {
        return new MessageResult();
    }
}
