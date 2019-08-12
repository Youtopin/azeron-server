package io.pinect.azeron.server.service.stateListener;

import io.pinect.azeron.server.service.initializer.MessagingInitializerService;
import lombok.extern.log4j.Log4j2;
import nats.client.Nats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("natsConnectionStateListener")
@Log4j2
public class AzeronConnecionStateListener implements NatsConnectionStateListener{
    private final MessagingInitializerService messagingInitializerService;
    private State state;

    @Autowired
    public AzeronConnecionStateListener(MessagingInitializerService messagingInitializerService) {
        this.messagingInitializerService = messagingInitializerService;
    }

    @Override
    public State getCurrentState() {
        return this.state;
    }

    @Override
    public void onConnectionStateChange(Nats nats, State state) {
        log.trace("Nats client state changed to "+ state.name());

        switch (state){
            case CONNECTED:
                messagingInitializerService.init(nats);
                break;
        }

        this.state = state;
    }
}
