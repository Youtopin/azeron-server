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
        log.trace("Added new message to repository -> "+ messageEntity);
    }

    @Override
    public MessageEntity seenMessage(String messageId, String serviceName) {
        log.trace("Added seem to message in repository -> messageId: "+ messageId + ", serviceName: "+ serviceName);
        return new MessageEntity();
    }

    @Override
    public void seenMessages(List<String> messageIds, String serviceName) {
        log.trace("Added seem to message in repository -> messageIds: "+ messageIds+ ", serviceNames: "+ serviceName);
    }

    @Override
    public MessageEntity removeMessage(String messageId) {
        log.trace("Removing message from repository -> messageId: "+ messageId);
        return new MessageEntity();
    }

    @Override
    public MessageResult getUnseenMessagesOfService(String serviceName, int offset, int limit) {
        log.trace("Getting unseen messages from repository -> serviceName: "+ serviceName);
        return new MessageResult();
    }
}
