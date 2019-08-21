package io.pinect.azeron.server.service.stateListener;

import io.pinect.azeron.server.service.initializer.MessagingInitializerService;
import lombok.extern.log4j.Log4j2;
import nats.client.Nats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service("natsConnectionStateListener")
@Log4j2
public class AzeronNatsConnecionStateListener implements NatsConnectionStateListener{
    private final MessagingInitializerService messagingInitializerService;
    private State state = State.DISCONNECTED;

    @Autowired
    public AzeronNatsConnecionStateListener(MessagingInitializerService messagingInitializerService) {
        this.messagingInitializerService = messagingInitializerService;
    }

    @Override
    public State getCurrentState() {
        return this.state;
    }

    @Override
    public void onConnectionStateChange(Nats nats, State state) {
        log.info("Nats state changed from "+ this.state + " to fucking "+ state);

        switch (state){
            case CONNECTED:
                if(this.state.equals(State.DISCONNECTED))
                    messagingInitializerService.init(nats);
                break;
        }

        this.state = state;
    }
}
