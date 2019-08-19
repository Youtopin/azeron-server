package io.pinect.azeron.server.service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pinect.azeron.server.config.ChannelName;
import io.pinect.azeron.server.config.properties.AzeronServerNatsProperties;
import io.pinect.azeron.server.domain.dto.AzeronNetworkMessageDto;
import io.pinect.azeron.server.domain.dto.out.InfoPublishDto;
import io.pinect.azeron.server.domain.model.AzeronServerInfo;
import io.pinect.azeron.server.service.tracker.ClientTracker;
import lombok.extern.log4j.Log4j2;
import nats.client.Nats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AzeronInfoMessagePublisher {
    private final ClientTracker clientTracker;
    private final ObjectMapper objectMapper;
    private final AzeronServerInfo azeronServerInfo;
    private final AzeronServerNatsProperties azeronServerNatsProperties;

    @Autowired
    public AzeronInfoMessagePublisher(ClientTracker clientTracker, ObjectMapper objectMapper, AzeronServerInfo azeronServerInfo, AzeronServerNatsProperties azeronServerNatsProperties) {
        this.clientTracker = clientTracker;
        this.objectMapper = objectMapper;
        this.azeronServerInfo = azeronServerInfo;
        this.azeronServerNatsProperties = azeronServerNatsProperties;
    }

    public void publishInfoMessage(Nats nats){
        try {
            int channelsSize = clientTracker.getChannelToClientConfigsMap().size();
            InfoPublishDto infoPublishDto = getInfoPublishDto(channelsSize);
            String value = objectMapper.writeValueAsString(infoPublishDto);
            nats.publish(ChannelName.AZERON_MAIN_CHANNEL_NAME, value);
        }catch (Exception e){
            log.error(e);
        }
    }

    private InfoPublishDto getInfoPublishDto(int channelsSize) {
        InfoPublishDto infoPublishDto = InfoPublishDto.builder()
                .channelsCount(channelsSize)
                .nats(azeronServerNatsProperties)
                .build();

        infoPublishDto.setServerUUID(azeronServerInfo.getId());
        infoPublishDto.setType(AzeronNetworkMessageDto.MessageType.INFO);
        return infoPublishDto;
    }
}
