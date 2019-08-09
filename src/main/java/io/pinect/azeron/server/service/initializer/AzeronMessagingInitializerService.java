package io.pinect.azeron.server.service.initializer;

import io.pinect.azeron.server.service.handler.AzeronFetchRequestMessageHandler;
import io.pinect.azeron.server.service.handler.SubscribeMessageHandler;
import io.pinect.azeron.server.service.handler.UnSubscribeMessageHandler;
import io.pinect.azeron.server.service.publisher.AzeronFetchMessagePublisher;
import nats.client.Nats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.pinect.azeron.server.config.ChannelName.*;

@Service("messagingInitializerService")
public class AzeronMessagingInitializerService implements MessagingInitializerService {
    private final AzeronFetchMessagePublisher azeronFetchMessagePublisher;
    private final AzeronFetchRequestMessageHandler azeronFetchMessageHandlerMessageHandler;
    private final SubscribeMessageHandler subscribeMessageHandler;
    private final UnSubscribeMessageHandler unsubscribeMessageHandler;
    private Nats nats;

    @Autowired
    public AzeronMessagingInitializerService(AzeronFetchMessagePublisher azeronFetchMessagePublisher, AzeronFetchRequestMessageHandler azeronFetchMessageHandlerMessageHandler, SubscribeMessageHandler subscribeMessageHandler, UnSubscribeMessageHandler unsubscribeMessageHandler) {
        this.azeronFetchMessagePublisher = azeronFetchMessagePublisher;
        this.azeronFetchMessageHandlerMessageHandler = azeronFetchMessageHandlerMessageHandler;
        this.subscribeMessageHandler = subscribeMessageHandler;
        this.unsubscribeMessageHandler = unsubscribeMessageHandler;
    }

    @Override
    public void init(Nats nats) {
        setNats(nats);
        fetchNetworkSubscription();
        fetchSubscriptions();
    }

    public void setNats(Nats nats) {
        this.nats = nats;
    }

    private void fetchNetworkSubscription(){
        nats.subscribe(AZERON_MAIN_CHANNEL_NAME, azeronFetchMessageHandlerMessageHandler);
        azeronFetchMessagePublisher.publishFetchMessage(nats);
    }

    private void fetchSubscriptions(){
        nats.subscribe(AZERON_SUBSCRIBE_API_NAME, subscribeMessageHandler);
        nats.subscribe(AZERON_UNSUBSCRIBE_API_NAME, unsubscribeMessageHandler);
    }
}
