package io.pinect.azeron.server.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pinect.azeron.server.domain.dto.in.MessageDto;
import io.pinect.azeron.server.domain.dto.in.SubscriptionControlDto;
import io.pinect.azeron.server.domain.entity.MessageEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Configuration
@Log4j2
public class DtoConverter {
    private final ObjectMapper objectMapper;

    @Autowired
    public DtoConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean("toSubscriptionControlConverter")
    public Converter<String, SubscriptionControlDto> toSubscriptionControl(){
        return new Converter<String, SubscriptionControlDto>() {
            @Override
            public SubscriptionControlDto convert(String s) {
                try {
                    return objectMapper.readValue(s, SubscriptionControlDto.class);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to convert input string to SubscriptionControlDto", e);
                }
            }
        };
    }

    @Bean("entityToMessageDtoListConverter")
    public Converter<Collection<MessageEntity>, List<MessageDto>> toMessageDtoList(){
        return new Converter<Collection<MessageEntity>, List<MessageDto>>() {
            @Override
            public List<MessageDto> convert(Collection<MessageEntity> messageEntities) {
                List<MessageDto> messageDtos = new ArrayList<>();
                messageEntities.forEach(messageEntity -> {
                    messageDtos.add(toMessageDto().convert(messageEntity));
                });
                return messageDtos;
            }
        };
    }

    @Bean("entityToMessageDtoConverter")
    public Converter<MessageEntity, MessageDto> toMessageDto(){
        return new Converter<MessageEntity, MessageDto>() {
            @Override
            public MessageDto convert(MessageEntity messageEntity) {
                try {
                    return MessageDto.builder()
                            .channelName(messageEntity.getChannel())
                            .messageId(messageEntity.getMessageId())
                            .object(objectMapper.readValue(messageEntity.getMessage(), JsonNode.class))
                            .timeStamp(messageEntity.getDate().getTime())
                            .build();
                } catch (IOException e) {
                    log.error(e);
                    return null;
                }
            }
        };
    }

    @Bean("toMessageEntityConverter")
    public Converter<MessageDto, MessageEntity> toMessageEntity(){
        return new Converter<MessageDto, MessageEntity>() {
            @Override
            public MessageEntity convert(MessageDto messageDto) {
                return MessageEntity.builder()
                        .messageId(messageDto.getMessageId())
                        .date(new Date(messageDto.getTimeStamp()))
                        .message(messageDto.getObject().toString())
                        .channel(messageDto.getChannelName())
                        .sender(messageDto.getServiceName())
                        .build();
            }
        };
    }

    @Bean("toMessageConverter")
    public Converter<String, MessageDto> toMessage(){
        return new Converter<String, MessageDto>() {
            @Override
            public MessageDto convert(String s) {
                try {
                    return objectMapper.readValue(s, MessageDto.class);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to convert input string to SubscriptionControlDto", e);
                }
            }
        };
    }
}
