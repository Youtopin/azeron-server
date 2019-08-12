package io.pinect.azeron.server.service.initializer;

import io.pinect.azeron.server.service.handler.AzeronNetworkMessageMessageHandler;
import io.pinect.azeron.server.service.handler.SubscribeMessageHandler;
import io.pinect.azeron.server.service.handler.UnSubscribeMessageHandler;
import io.pinect.azeron.server.service.publisher.AzeronFetchMessagePublisher;
import lombok.extern.log4j.Log4j2;
import nats.client.Nats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.pinect.azeron.server.config.ChannelName.*;

@Service("messagingInitializerService")
@Log4j2
public class AzeronMessagingInitializerService implements MessagingInitializerService {
    private final AzeronFetchMessagePublisher azeronFetchMessagePublisher;
    private final AzeronNetworkMessageMessageHandler azeronFetchMessageHandlerMessageHandler;
    private final SubscribeMessageHandler subscribeMessageHandler;
    private final UnSubscribeMessageHandler unsubscribeMessageHandler;
    private Nats nats;

    @Autowired
    public AzeronMessagingInitializerService(AzeronFetchMessagePublisher azeronFetchMessagePublisher, AzeronNetworkMessageMessageHandler azeronFetchMessageHandlerMessageHandler, SubscribeMessageHandler subscribeMessageHandler, UnSubscribeMessageHandler unsubscribeMessageHandler) {
        this.azeronFetchMessagePublisher = azeronFetchMessagePublisher;
        this.azeronFetchMessageHandlerMessageHandler = azeronFetchMessageHandlerMessageHandler;
        this.subscribeMessageHandler = subscribeMessageHandler;
        this.unsubscribeMessageHandler = unsubscribeMessageHandler;
    }

    @Override
    public void init(Nats nats) {
        log.trace("(re)initializing Azeron Server");
        setNats(nats);
        fetchNetworkSubscription();
        fetchSubscriptions();
    }

    public void setNats(Nats nats) {
        this.nats = nats;
    }

    private void fetchNetworkSubscription(){
        log.trace("Fetching Azeron network subscription");
        nats.subscribe(AZERON_MAIN_CHANNEL_NAME, azeronFetchMessageHandlerMessageHandler);
        azeronFetchMessagePublisher.publishFetchMessage(nats);
    }

    private void fetchSubscriptions(){
        log.trace("Fetching channel s/u subscription");

        nats.subscribe(AZERON_SUBSCRIBE_API_NAME, subscribeMessageHandler);
        nats.subscribe(AZERON_UNSUBSCRIBE_API_NAME, unsubscribeMessageHandler);
    }
}
