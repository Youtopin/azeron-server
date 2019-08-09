package io.pinect.azeron.server.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pinect.azeron.server.config.ChannelName;
import io.pinect.azeron.server.domain.dto.AzeronFetchRequestDto;
import io.pinect.azeron.server.domain.model.AzeronServerInfo;
import io.pinect.azeron.server.service.handler.FetchResponseMessageHandler;
import nats.client.Nats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AzeronFetchMessagePublisher {
    private final AzeronServerInfo azeronServerInfo;
    private final FetchResponseMessageHandler fetchResponseMessageHandler;
    private final ObjectMapper objectMapper;

    @Autowired
    public AzeronFetchMessagePublisher(AzeronServerInfo azeronServerInfo, FetchResponseMessageHandler fetchResponseMessageHandler, ObjectMapper objectMapper) {
        this.azeronServerInfo = azeronServerInfo;
        this.fetchResponseMessageHandler = fetchResponseMessageHandler;
        this.objectMapper = objectMapper;
    }

    public void publishFetchMessage(Nats nats){
        AzeronFetchRequestDto azeronFetchRequestDto = new AzeronFetchRequestDto(azeronServerInfo.getId());
        String json = null;
        try {
            json = objectMapper.writeValueAsString(azeronFetchRequestDto);
            nats.request(ChannelName.AZERON_MAIN_CHANNEL_NAME, json, 1, TimeUnit.MINUTES, fetchResponseMessageHandler);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
