package io.pinect.azeron.server.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pinect.azeron.server.config.properties.AzeronServerProperties;
import io.pinect.azeron.server.domain.dto.BasicAzeronReponseDto;
import io.pinect.azeron.server.domain.dto.ResponseStatus;
import io.pinect.azeron.server.domain.dto.in.MessageDto;
import io.pinect.azeron.server.domain.dto.in.UnseenQueryDto;
import io.pinect.azeron.server.domain.dto.out.UnseenResponseDto;
import io.pinect.azeron.server.domain.entity.MessageEntity;
import io.pinect.azeron.server.domain.repository.MessageRepository;
import lombok.extern.log4j.Log4j2;
import nats.client.Message;
import nats.client.MessageHandler;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class AzeronQueryMessageHandler implements MessageHandler {
    private final MessageRepository messageRepository;
    private final AzeronServerProperties azeronServerProperties;
    private final Converter<Collection<MessageEntity>, List<MessageDto>> entityToMessageDtoListConverter;
    private final ObjectMapper objectMapper;

    public AzeronQueryMessageHandler(MessageRepository messageRepository, AzeronServerProperties azeronServerProperties, Converter<Collection<MessageEntity>, List<MessageDto>> entityToMessageDtoListConverter, ObjectMapper objectMapper) {
        this.messageRepository = messageRepository;
        this.azeronServerProperties = azeronServerProperties;
        this.entityToMessageDtoListConverter = entityToMessageDtoListConverter;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message) {
        if(!message.isRequest())
            return;
        try {
            UnseenQueryDto unseenQueryDto = objectMapper.readValue(message.getBody(), UnseenQueryDto.class);
            MessageRepository.MessageResult messageResult = messageRepository.getUnseenMessagesOfService(unseenQueryDto.getServiceName(), 0, azeronServerProperties.getUnseenQueryLimit(), new Date(unseenQueryDto.getDateBefore()));
            List<MessageDto> messageDtos = entityToMessageDtoListConverter.convert(messageResult.getMessages());
            UnseenResponseDto unseenResponseDto = UnseenResponseDto.builder()
                    .hasMore(messageResult.isHasMore())
                    .count(messageDtos != null ? messageDtos.size() : 0)
                    .messages(messageDtos)
                    .build();
            message.reply(objectMapper.writeValueAsString(unseenResponseDto), 15, TimeUnit.SECONDS);
        } catch (IOException e) {
            log.error(e);
            try {
                message.reply(objectMapper.writeValueAsString(new BasicAzeronReponseDto(ResponseStatus.FAILED)));
            } catch (JsonProcessingException ignored) {}
        }
    }
}
