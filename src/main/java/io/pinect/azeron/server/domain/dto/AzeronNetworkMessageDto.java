package io.pinect.azeron.server.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AzeronNetworkMessageDto {
    private String serverUUID;
    private String clientUUID;
    private MessageType type;

    public AzeronNetworkMessageDto(String serverUUID, String clientUUID, MessageType type) {
        this.serverUUID = serverUUID;
        this.clientUUID = clientUUID;
        this.type = type;
    }

    public AzeronNetworkMessageDto(String serverUUID, MessageType type) {
        this.serverUUID = serverUUID;
        this.type = type;
    }

    public AzeronNetworkMessageDto(MessageType messageType) {
        this.type = messageType;
    }

    public enum MessageType {
        FETCH_REQUEST, FETCH_RESPONSE, PING
    }
}
