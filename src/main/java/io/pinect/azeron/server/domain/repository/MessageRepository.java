package io.pinect.azeron.server.domain.repository;

import io.pinect.azeron.server.domain.entity.MessageEntity;
import lombok.*;

import java.util.Date;
import java.util.List;

public interface MessageRepository {
    MessageEntity addMessage(MessageEntity messageEntity);
    MessageEntity seenMessage(String messageId, String serviceName);
    void seenMessages(List<String> messageId, String serviceName);
    void removeMessage(String messageId);
    MessageResult getUnseenMessagesOfService(String serviceName, int offset, int limit, Date before);

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class MessageResult {
        List<MessageEntity> messages;
        boolean hasMore;
    }
}
