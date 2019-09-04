package io.pinect.azeron.server.domain.entity;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private List<String> subscribers = new CopyOnWriteArrayList<>();
    @Builder.Default
    private List<String> seenSubscribers = new CopyOnWriteArrayList<>();
    private int seenNeeded = 0;
    private int seenCount = 0;
    private Date date = new Date();
    private boolean completed;

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
