package io.pinect.azeron.server.config;

import io.pinect.azeron.server.service.tracker.ClientStateListenerService;
import io.pinect.azeron.server.service.tracker.ClientTracker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class AzeronApplicationStartupListener implements ApplicationListener<ApplicationStartedEvent> {
    private final ClientTracker clientTracker;
    private final ClientStateListenerService clientStateListenerService;


    @Autowired
    public AzeronApplicationStartupListener(ClientTracker clientTracker, ClientStateListenerService clientStateListenerService) {
        this.clientTracker = clientTracker;
        this.clientStateListenerService = clientStateListenerService;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        clientTracker.addListener(clientStateListenerService);
    }

}
