package io.pinect.azeron.server.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.pinect.azeron.server.domain.dto.AzeronNetworkMessageDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AzeronFetchRequestDto extends AzeronNetworkMessageDto {
    public AzeronFetchRequestDto(String serverUUID) {
        super(serverUUID,MessageType.FETCH_REQUEST);
    }

    public AzeronFetchRequestDto() {
        super(MessageType.FETCH_REQUEST);
    }
}
