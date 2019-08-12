package io.pinect.azeron.server.domain.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class MessageEntity implements Serializable {
    private String channel;
    private String messageId;
    private String message;
    private String sender;
    private List<String> subscribers;
    private List<String> seenSubscribers;
    private int seenNeeded;
    private int seenCount;
    private Date date;
    private boolean completed;
}
