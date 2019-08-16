package io.pinect.azeron.server.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AzeronNetworkMessageDto {
    private String serverUUID;
    private String clientUUID;
    private MessageType type;

    public AzeronNetworkMessageDto() {
    }

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
        FETCH_REQUEST, FETCH_RESPONSE, PING, INFO
    }
}
