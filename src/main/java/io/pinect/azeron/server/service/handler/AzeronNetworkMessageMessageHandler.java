package io.pinect.azeron.server.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pinect.azeron.server.domain.dto.AzeronNetworkMessageDto;
import io.pinect.azeron.server.domain.dto.in.AzeronFetchRequestDto;
import io.pinect.azeron.server.domain.dto.out.AzeronChannelListDto;
import io.pinect.azeron.server.domain.dto.out.InfoPublishDto;
import io.pinect.azeron.server.domain.model.AzeronServerInfo;
import io.pinect.azeron.server.service.FetchService;
import io.pinect.azeron.server.service.InfoService;
import nats.client.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Message handler to handle requests to fetch services map between all azeron servers
 */
@Component
public class AzeronNetworkMessageMessageHandler extends AbstractMessageHandler {
    private final ObjectMapper objectMapper;
    private final FetchService fetchService;
    private final InfoService infoService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public AzeronNetworkMessageMessageHandler(ObjectMapper objectMapper, FetchService fetchService, InfoService infoService) {
        this.objectMapper = objectMapper;
        this.fetchService = fetchService;
        this.infoService = infoService;
    }

    @Override
    public void onMessage(Message message) {
        super.onMessage(message);
        String body = message.getBody();
        try {
            AzeronNetworkMessageDto azeronNetworkMessageDto = objectMapper.readValue(body, AzeronNetworkMessageDto.class);
            String reponse = null;

            switch (azeronNetworkMessageDto.getType()){
                case FETCH_REQUEST:
                    AzeronFetchRequestDto azeronFetchRequestDto = objectMapper.readValue(body, AzeronFetchRequestDto.class);
                    AzeronChannelListDto azeronChannelListDto = fetchService.fetch(azeronFetchRequestDto);
                    if(azeronChannelListDto != null)
                        reponse = objectMapper.writeValueAsString(azeronChannelListDto);
                    break;
                case INFO:
                    InfoPublishDto infoPublishDto = objectMapper.readValue(body, InfoPublishDto.class);
                    infoService.addInfo(infoPublishDto);
                    break;
            }

            if(message.isRequest() && reponse != null)
                message.reply(reponse, 5, TimeUnit.SECONDS);
        } catch (IOException e) {
            logger.error("could not read value of json: "+ message.getBody() , e);
        }
    }


}
