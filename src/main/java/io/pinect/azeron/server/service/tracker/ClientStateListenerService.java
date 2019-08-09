package io.pinect.azeron.server.service.tracker;

import io.pinect.azeron.server.AtomicNatsHolder;
import io.pinect.azeron.server.domain.model.ClientConfig;
import io.pinect.azeron.server.service.handler.AzeronMessageHandler;
import nats.client.Nats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientStateListenerService implements ClientTracker.ClientStateListener {
    private final AzeronMessageHandler azeronMessageHandler;
    private final Nats nats;

    @Autowired
    public ClientStateListenerService(AzeronMessageHandler azeronMessageHandler, @Lazy AtomicNatsHolder atomicNatsHolder) {
        this.azeronMessageHandler = azeronMessageHandler;
        this.nats = atomicNatsHolder.getNatsAtomicReference().get();
    }

    @Override
    public void onCreate(String channelName, ClientConfig clientConfig) {

    }

    @Override
    public void onDelete(String serviceName, String channelName) {

    }

    @Override
    public void onDelete(String serviceName, List<String> channelNames) {

    }
}
