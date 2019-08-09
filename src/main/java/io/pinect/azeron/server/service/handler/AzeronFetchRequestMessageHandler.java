package io.pinect.azeron.server.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pinect.azeron.server.domain.dto.AzeronChannelListDto;
import io.pinect.azeron.server.domain.dto.AzeronFetchRequestDto;
import io.pinect.azeron.server.domain.model.AzeronServerInfo;
import io.pinect.azeron.server.domain.model.ClientConfig;
import io.pinect.azeron.server.service.tracker.ClientTracker;
import nats.client.Message;
import nats.client.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Message handler to handle requests to fetch services map between all azeron servers
 */
@Component
public class AzeronFetchRequestMessageHandler implements MessageHandler {
    private final ClientTracker clientTracker;
    private final AzeronServerInfo azeronServerInfo;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public AzeronFetchRequestMessageHandler(ClientTracker clientTracker, AzeronServerInfo azeronServerInfo, ObjectMapper objectMapper) {
        this.clientTracker = clientTracker;
        this.azeronServerInfo = azeronServerInfo;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message) {
        if(message.isRequest()){
            String body = message.getBody();
            try {
                AzeronFetchRequestDto azeronFetchRequestDto = objectMapper.readValue(body, AzeronFetchRequestDto.class);
                if(azeronFetchRequestDto.getServerUUID().equals(azeronServerInfo.getId()))
                    return;

                String jsonFromChannelsMap = getJsonFromChannelsMap();
                message.reply(jsonFromChannelsMap, 5, TimeUnit.SECONDS);
            } catch (IOException e) {
                logger.error("could not read value of json: "+ message.getBody() , e);
            }
        }
    }

    private String getJsonFromChannelsMap() throws JsonProcessingException {
        Map<String, List<ClientConfig>> channelsToConfigsMap = clientTracker.getChannelToClientConfigsMap();
        List<AzeronChannelListDto.Channel> channels = new ArrayList<>();
        for(String channelName: channelsToConfigsMap.keySet()){
            AzeronChannelListDto.Channel channel = new AzeronChannelListDto.Channel(channelName, channelsToConfigsMap.get(channelName));
            channels.add(channel);
        }
        AzeronChannelListDto azeronChannelListDto = new AzeronChannelListDto(azeronServerInfo.getId(), azeronServerInfo.getVersion(), channels);
        return objectMapper.writeValueAsString(azeronChannelListDto);
    }
}