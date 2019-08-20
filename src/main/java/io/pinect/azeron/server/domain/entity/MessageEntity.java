package io.pinect.azeron.server.domain.entity;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageEntity implements Serializable {
    private String channel;
    private String messageId;
    private String message;
    private String sender;
    private List<String> subscribers;
    @Builder.Default
    private List<String> seenSubscribers = new ArrayList<>();
    private int seenNeeded;
    private int seenCount;
    private Date date;
    private boolean completed;
}
