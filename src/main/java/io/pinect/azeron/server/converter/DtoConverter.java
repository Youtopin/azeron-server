package io.pinect.azeron.server.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pinect.azeron.server.domain.dto.SubscriptionControlDto;
import io.pinect.azeron.server.domain.dto.MessageDto;
import io.pinect.azeron.server.domain.entity.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.util.Date;

@Configuration
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
