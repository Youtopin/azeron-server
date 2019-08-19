package io.pinect.azeron.server.service.tracker;

import io.pinect.azeron.server.AtomicNatsHolder;
import io.pinect.azeron.server.domain.model.ClientConfig;
import io.pinect.azeron.server.service.handler.AzeronMessageHandler;
import lombok.extern.log4j.Log4j2;
import nats.client.Nats;
import nats.client.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Listens to ClientTracker events and handles subscription add/remove
 */
@Service
@Log4j2
public class ClientStateListenerService implements ClientTracker.ClientStateListener {
    private final AzeronMessageHandler azeronMessageHandler;
    private final AtomicNatsHolder atomicNatsHolder;
    private final Map<String, Subscription> channelToSubscriptionMap = new ConcurrentHashMap<>();

    @Autowired
    public ClientStateListenerService(@Lazy AzeronMessageHandler azeronMessageHandler, @Lazy AtomicNatsHolder atomicNatsHolder) {
        this.azeronMessageHandler = azeronMessageHandler;
        this.atomicNatsHolder = atomicNatsHolder;
    }

    @Override
    public void onCreate(ClientTracker clientTracker, String channelName, ClientConfig clientConfig) {
        log.trace("New subscription for channel "+ channelName + " -> " + clientConfig);

        channelToSubscriptionMap.putIfAbsent(channelName, getNats().subscribe(
                channelName,
                azeronMessageHandler
        ));
    }

    @Override
    public void onDelete(ClientTracker clientTracker, String serviceName, String channelName) {
        log.trace("Un-subscribing "+ serviceName +" from channel "+ channelName);

        List<String> servicesOfChannel = clientTracker.getServicesOfChannel(channelName);
        if(servicesOfChannel.size() != 0)
            return;
        Subscription subscription = channelToSubscriptionMap.remove(channelName);
        subscription.close();
    }

    @Override
    public void onDelete(ClientTracker clientTracker, String serviceName, List<String> channelNames) {
        log.trace("Un-subscribing "+ serviceName +" from channels "+ channelNames);

        for(String channelName: channelNames){
            onDelete(clientTracker, serviceName, channelName);
        }
    }

    private Nats getNats(){
        return atomicNatsHolder.getNatsAtomicReference().get();
    }
}
