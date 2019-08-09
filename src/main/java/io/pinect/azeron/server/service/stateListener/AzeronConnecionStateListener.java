package io.pinect.azeron.server.service.stateListener;

import io.pinect.azeron.server.service.initializer.MessagingInitializerService;
import nats.client.Nats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("natsConnectionStateListener")
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
        switch (state){
            case CONNECTED:
                messagingInitializerService.init(nats);
                break;
        }

        this.state = state;
    }
}
