package io.pinect.azeron.server.domain.dto;

public class AzeronFetchRequestDto extends AzeronNetworkMessageDto {
    public AzeronFetchRequestDto(String serverUUID) {
        super(serverUUID,MessageType.FETCH_REQUEST);
    }

    public AzeronFetchRequestDto() {
        super(MessageType.FETCH_REQUEST);
    }
}
