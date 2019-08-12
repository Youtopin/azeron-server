package io.pinect.azeron.server.service.handler;

import lombok.extern.log4j.Log4j2;
import nats.client.Message;
import nats.client.MessageHandler;

@Log4j2
public class AbstractMessageHandler implements MessageHandler {
    @Override
    public void onMessage(Message message) {
        log.trace("Message received in channel `"+ message.getSubject() + "` -> " + message.getBody());
    }
}
