package io.pinect.azeron.server.domain.repository;

import io.pinect.azeron.server.domain.entity.MessageEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public interface MessageRepository {
    void addMessage(MessageEntity messageEntity);
    MessageEntity seenMessage(String messageId, String serviceName);
    void seenMessages(List<String> messageId, String serviceName);
    MessageEntity removeMessage(String messageId);
    MessageResult getUnseenMessagesOfService(String serviceName);

    @Getter
    @Setter
    @NoArgsConstructor
    class MessageResult {
        List<MessageEntity> messages;
        boolean hasMore;
    }
}
