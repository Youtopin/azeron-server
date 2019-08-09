package io.pinect.azeron.server.service.handler;

import io.pinect.azeron.server.domain.dto.SubscriptionControlDto;
import io.pinect.azeron.server.service.tracker.ClientTracker;
import lombok.extern.log4j.Log4j2;
import nats.client.Message;
import nats.client.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class SubscribeMessageHandler implements MessageHandler {
    private final ClientTracker clientTracker;
    private final Converter<String, SubscriptionControlDto> toSubscriptionControlConverter;

    @Autowired
    public SubscribeMessageHandler(ClientTracker clientTracker, Converter<String, SubscriptionControlDto> toSubscriptionControlConverter) {
        this.clientTracker = clientTracker;
        this.toSubscriptionControlConverter = toSubscriptionControlConverter;
    }

    @Override
    public void onMessage(Message message) {
        try {
            SubscriptionControlDto subscriptionControlDto = toSubscriptionControlConverter.convert(message.getBody());
            assert subscriptionControlDto != null;
            clientTracker.addClient(subscriptionControlDto.getChannelName(), subscriptionControlDto.getConfig());
            message.reply("OK");
        }catch (Exception e){
            message.reply("FAILED");
            log.error("Caught exception while processing message", e);
        }
    }
}
