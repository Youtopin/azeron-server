package io.pinect.azeron.server.domain.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.pinect.azeron.server.domain.dto.AzeronNetworkMessageDto;
import io.pinect.azeron.server.domain.model.ClientConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AzeronChannelListDto extends AzeronNetworkMessageDto {
    private double version;
    private List<Channel> channels;

    public AzeronChannelListDto(String serverUUID) {
        super(serverUUID, MessageType.FETCH_RESPONSE);
    }

    public AzeronChannelListDto() {
        super(MessageType.FETCH_RESPONSE);
    }

    public AzeronChannelListDto(String serverUUID, double version, List<Channel> channels) {
        this(serverUUID);
        this.version = version;
        this.channels = channels;
    }

    public AzeronChannelListDto(int version, List<Channel> channels) {
        this();
        this.version = version;
        this.channels = channels;
    }

    @Getter
    @Setter
    public static class Channel {
        private String name;
        private List<ClientConfig> configs;

        public Channel(String name, List<ClientConfig> configs) {
            this.name = name;
            this.configs = configs;
        }
    }
}
