package io.pinect.azeron.server.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AzeronFetchRequestDto extends AzeronNetworkMessageDto {
    public AzeronFetchRequestDto(String serverUUID) {
        super(serverUUID,MessageType.FETCH_REQUEST);
    }

    public AzeronFetchRequestDto() {
        super(MessageType.FETCH_REQUEST);
    }
}
