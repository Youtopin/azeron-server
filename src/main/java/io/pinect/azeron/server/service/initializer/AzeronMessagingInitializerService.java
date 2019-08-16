package io.pinect.azeron.server.service.initializer;

import io.pinect.azeron.server.config.properties.AzeronServerProperties;
import io.pinect.azeron.server.service.handler.*;
import io.pinect.azeron.server.service.publisher.AzeronFetchMessagePublisher;
import io.pinect.azeron.server.service.publisher.AzeronInfoMessagePublisher;
import lombok.extern.log4j.Log4j2;
import nats.client.Nats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static io.pinect.azeron.server.config.ChannelName.*;

@Service("messagingInitializerService")
@Log4j2
public class AzeronMessagingInitializerService implements MessagingInitializerService {
    private final AzeronFetchMessagePublisher azeronFetchMessagePublisher;
    private final AzeronInfoMessagePublisher azeronInfoMessagePublisher;
    private final AzeronNetworkMessageMessageHandler azeronNetworkMessageMessageHandler;
    private final SubscribeMessageHandler subscribeMessageHandler;
    private final UnSubscribeMessageHandler unsubscribeMessageHandler;
    private final AzeronSeenMessageHandler azeronSeenMessageHandler;
    private final AzeronQueryMessageHandler azeronQueryMessageHandler;
    private final AzeronServerProperties azeronServerProperties;
    private final TaskScheduler azeronTaskScheduler;
    private ScheduledFuture<?> schedule;
    private Nats nats;

    @Autowired
    public AzeronMessagingInitializerService(AzeronFetchMessagePublisher azeronFetchMessagePublisher, AzeronInfoMessagePublisher azeronInfoMessagePublisher, AzeronNetworkMessageMessageHandler azeronNetworkMessageMessageHandler, SubscribeMessageHandler subscribeMessageHandler, UnSubscribeMessageHandler unsubscribeMessageHandler, AzeronSeenMessageHandler azeronSeenMessageHandler, AzeronQueryMessageHandler azeronQueryMessageHandler, AzeronServerProperties azeronServerProperties, TaskScheduler azeronTaskScheduler) {
        this.azeronFetchMessagePublisher = azeronFetchMessagePublisher;
        this.azeronInfoMessagePublisher = azeronInfoMessagePublisher;
        this.azeronNetworkMessageMessageHandler = azeronNetworkMessageMessageHandler;
        this.subscribeMessageHandler = subscribeMessageHandler;
        this.unsubscribeMessageHandler = unsubscribeMessageHandler;
        this.azeronSeenMessageHandler = azeronSeenMessageHandler;
        this.azeronQueryMessageHandler = azeronQueryMessageHandler;
        this.azeronServerProperties = azeronServerProperties;
        this.azeronTaskScheduler = azeronTaskScheduler;
    }

    @Override
    public void init(Nats nats) {
        log.trace("(re)initializing Azeron Server");
        setNats(nats);
        fetchNetworkSubscription();
        fetchSubscriptions();
        fetchQueryHandler();
        fetchInfoSync();
        fetchChannelSync();
    }

    private void fetchQueryHandler() {
        log.trace("Fetching query handler");
        nats.subscribe(AZERON_QUERY_CHANNEL_NAME, azeronServerProperties.getQueueName(), azeronQueryMessageHandler);
    }

    public synchronized void setNats(Nats nats) {
        this.nats = nats;
    }

    private void fetchNetworkSubscription(){
        log.trace("Fetching Azeron network subscription");
        nats.subscribe(AZERON_MAIN_CHANNEL_NAME, azeronNetworkMessageMessageHandler);
        azeronFetchMessagePublisher.publishFetchMessage(nats);
    }

    private void fetchSubscriptions(){
        log.trace("Fetching channel s/u subscription");

        nats.subscribe(AZERON_SUBSCRIBE_API_NAME, subscribeMessageHandler);
        nats.subscribe(AZERON_UNSUBSCRIBE_API_NAME, unsubscribeMessageHandler);
        nats.subscribe(AZERON_SEEN_CHANNEL_NAME, azeronServerProperties.getQueueName(), azeronSeenMessageHandler);
    }

    private void fetchInfoSync(){
        if(this.schedule != null)
            schedule.cancel(true);
        PeriodicTrigger periodicTrigger = new PeriodicTrigger(azeronServerProperties.getInfoSyncIntervalSeconds(), TimeUnit.SECONDS);
        periodicTrigger.setInitialDelay(azeronServerProperties.getInfoSyncIntervalSeconds() * 1000);
        this.schedule = azeronTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                azeronInfoMessagePublisher.publishInfoMessage(nats);
            }
        }, periodicTrigger);
    }

    private void fetchChannelSync(){
        if(azeronServerProperties.isShouldSyncChannels()){
            PeriodicTrigger periodicTrigger = new PeriodicTrigger(azeronServerProperties.getChannelSyncIntervalSeconds(), TimeUnit.SECONDS);
            periodicTrigger.setInitialDelay(azeronServerProperties.getChannelSyncIntervalSeconds() * 1000);
            this.schedule = azeronTaskScheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    azeronFetchMessagePublisher.publishFetchMessage(nats);
                }
            }, periodicTrigger);
        }
    }
}
