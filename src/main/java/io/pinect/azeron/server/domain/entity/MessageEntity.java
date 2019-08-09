package io.pinect.azeron.server.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
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
