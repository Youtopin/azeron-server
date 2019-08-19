package io.pinect.azeron.server.service.handler;

import io.pinect.azeron.server.domain.dto.in.MessageDto;
import io.pinect.azeron.server.domain.entity.MessageEntity;
import io.pinect.azeron.server.domain.repository.MessageRepository;
import io.pinect.azeron.server.service.tracker.ClientTracker;
import lombok.extern.log4j.Log4j2;
import nats.client.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Log4j2
@Component
public class AzeronMessageHandler extends AbstractMessageHandler {
    private final ClientTracker clientTracker;
    private final MessageRepository messageRepository;
    private final Converter<String, MessageDto> toMessageConverter;
    private final Converter<MessageDto, MessageEntity> toMessageEntityConverter;

    @Autowired
    public AzeronMessageHandler(ClientTracker clientTracker, MessageRepository messageRepository, Converter<String, MessageDto> toMessageConverter, Converter<MessageDto, MessageEntity> toMessageEntityConverter) {
        this.clientTracker = clientTracker;
        this.messageRepository = messageRepository;
        this.toMessageConverter = toMessageConverter;
        this.toMessageEntityConverter = toMessageEntityConverter;
    }

    @Override
    public void onMessage(Message message) {
        super.onMessage(message);
        MessageDto messageDto = toMessageConverter.convert(message.getBody());
        assert messageDto != null;
        validateMessage(message, messageDto);
        List<String> servicesOfChannel = clientTracker.getServicesOfChannel(message.getSubject());
        processMessage(messageDto, servicesOfChannel);
    }

    private void processMessage(MessageDto messageDto, List<String> servicesOfChannel) {
        MessageEntity messageEntity = toMessageEntityConverter.convert(messageDto);
        assert messageEntity != null;
        messageEntity.setSubscribers(servicesOfChannel);
        messageEntity.setSeenNeeded(servicesOfChannel.size());
        messageRepository.addMessage(messageEntity);
    }

    private void validateMessage(Message message, MessageDto messageDto) {
        if(messageDto.getChannelName() != null && !message.getSubject().equals(messageDto.getChannelName()))
            log.warn("Message model channel name '"+ messageDto.getChannelName() + "' does not match nats subject "+ message.getSubject());
        Assert.notNull(messageDto.getObject(), "Message Object can not be null");
        Assert.notNull(messageDto.getMessageId(), "Message ID can not be null");
        Assert.hasText(messageDto.getMessageId(), "Message ID can not be empty");
        Assert.notNull(messageDto.getServiceName(), "Message Service name can not be null");
        Assert.hasText(messageDto.getServiceName(), "Message Service name can not be empty");
    }
}
