package io.pinect.azeron.server.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pinect.azeron.server.domain.dto.in.SeenDto;
import io.pinect.azeron.server.domain.dto.out.SeenResponseDto;
import io.pinect.azeron.server.service.SeenService;
import lombok.extern.log4j.Log4j2;
import nats.client.Message;
import nats.client.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class AzeronSeenMessageHandler implements MessageHandler {
    private final ObjectMapper objectMapper;
    private final SeenService seenService;

    @Autowired
    public AzeronSeenMessageHandler(ObjectMapper objectMapper, SeenService seenService) {
        this.objectMapper = objectMapper;
        this.seenService = seenService;
    }

    @Override
    public void onMessage(Message message) {
        SeenDto seenDto = getSeenDto(message.getBody());
        SeenResponseDto seenResponseDto = seenService.seen(seenDto);
        try {
            if(message.isRequest())
                message.reply(objectMapper.writeValueAsString(seenResponseDto));
            log.trace("sent seen response for reqId: "+ seenDto.getReqId());
        } catch (JsonProcessingException e) {
            log.error(e);
        }
    }



    private SeenDto getSeenDto(String body) {
        try {
            return objectMapper.readValue(body, SeenDto.class);
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException("Failed to cast message body to seen DTO",e);
        }
    }
}
