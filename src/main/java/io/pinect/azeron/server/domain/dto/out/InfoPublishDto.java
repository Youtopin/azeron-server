package io.pinect.azeron.server.domain.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.pinect.azeron.server.config.properties.AzeronServerNatsProperties;
import io.pinect.azeron.server.domain.dto.AzeronNetworkMessageDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
public class InfoPublishDto extends AzeronNetworkMessageDto {
    private AzeronServerNatsProperties nats;
    private int channelsCount;

    public InfoPublishDto() {
    }

    public InfoPublishDto(String serverUUID){
        super(serverUUID, MessageType.INFO);
    }
}
