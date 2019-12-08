package io.pinect.azeron.server.domain.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageEntity implements Serializable, Comparable<MessageEntity>{
    private String channel;
    private String messageId;
    private String message;
    private String sender;
    private Set<String> subscribers = new CopyOnWriteArraySet<>();
    @Builder.Default
    private Set<String> seenSubscribers = new CopyOnWriteArraySet<>();
    private int seenNeeded = 0;
    private int seenCount = 0;
    private Date date = new Date();
    private boolean completed;
    private boolean isDirty;


    public boolean isFullyAcknowledged(){
        return seenCount == seenNeeded || seenSubscribers.size() == subscribers.size();
    }

    public synchronized void increaseSeenCount(){
        seenCount++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageEntity that = (MessageEntity) o;
        return Objects.equals(channel, that.channel) &&
                Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(channel, messageId);
    }

    @Override
    public int compareTo(MessageEntity o) {
        if (getDate() == null || o.getDate() == null)
            return 0;
        return getDate().compareTo(o.getDate());
    }
}
