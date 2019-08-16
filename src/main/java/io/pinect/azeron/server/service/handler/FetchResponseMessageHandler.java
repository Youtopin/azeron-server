package io.pinect.azeron.server.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pinect.azeron.server.domain.dto.AzeronChannelListDto;
import io.pinect.azeron.server.domain.model.ClientConfig;
import io.pinect.azeron.server.service.tracker.ClientTracker;
import lombok.extern.log4j.Log4j2;
import nats.client.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class FetchResponseMessageHandler extends AbstractMessageHandler {
    private final ClientTracker clientTracker;
    private final ObjectMapper objectMapper;

    @Autowired
    public FetchResponseMessageHandler(ClientTracker clientTracker, ObjectMapper objectMapper) {
        this.clientTracker = clientTracker;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message) {
        super.onMessage(message);

        String body = message.getBody();
        try {
            AzeronChannelListDto azeronChannelListDto = objectMapper.readValue(body, AzeronChannelListDto.class);
            clientTracker.flush();
            for(AzeronChannelListDto.Channel channel: azeronChannelListDto.getChannels()){
                for(ClientConfig clientConfig: channel.getConfigs()){
                    clientTracker.addClient(channel.getName(), clientConfig);
                }
            }
        } catch (IOException e) {
            log.error("Could not read value of json: "+body, e);
        }
    }
}
