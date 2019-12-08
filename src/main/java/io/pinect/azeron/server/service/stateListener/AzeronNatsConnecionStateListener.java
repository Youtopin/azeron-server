package io.pinect.azeron.server.service.stateListener;

import io.pinect.azeron.server.service.initializer.MessagingInitializerService;
import lombok.extern.log4j.Log4j2;
import nats.client.Nats;
import org.springframework.beans.factory.annotation.Autowired;
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
    public synchronized void onConnectionStateChange(Nats nats, State state) {
        log.trace("Nats state changed from "+ this.state + " to "+ state);

        if(this.state != state && (state == State.SERVER_READY || state == State.CONNECTED)){
            messagingInitializerService.init(nats);
        }

        this.state = state;
    }
}
