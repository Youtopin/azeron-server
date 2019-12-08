package io.pinect.azeron.server.service.initializer;

import io.pinect.azeron.server.config.properties.AzeronServerNatsProperties;
import io.pinect.azeron.server.config.properties.AzeronServerProperties;
import io.pinect.azeron.server.domain.dto.out.InfoPublishDto;
import io.pinect.azeron.server.domain.model.AzeronServerInfo;
import io.pinect.azeron.server.service.InfoService;
import io.pinect.azeron.server.service.handler.*;
import io.pinect.azeron.server.service.publisher.AzeronFetchMessagePublisher;
import io.pinect.azeron.server.service.publisher.AzeronInfoMessagePublisher;
import io.pinect.azeron.server.service.tracker.ClientTracker;
import lombok.extern.log4j.Log4j2;
import nats.client.Nats;
import nats.client.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private final AzeronServerNatsProperties azeronServerNatsProperties;
    private final AzeronServerInfo azeronServerInfo;
    private final InfoService infoService;
    private final ClientTracker clientTracker;
    private final TaskScheduler azeronTaskScheduler;
    private ScheduledFuture<?> fetchInfoSchedule;
    private ScheduledFuture<?> fetchChannelSchedule;
    private List<Subscription> subscriptionList = new CopyOnWriteArrayList<>();
    private Nats nats;

    @Autowired
    public AzeronMessagingInitializerService(AzeronFetchMessagePublisher azeronFetchMessagePublisher, AzeronInfoMessagePublisher azeronInfoMessagePublisher, AzeronNetworkMessageMessageHandler azeronNetworkMessageMessageHandler, SubscribeMessageHandler subscribeMessageHandler, UnSubscribeMessageHandler unsubscribeMessageHandler, AzeronSeenMessageHandler azeronSeenMessageHandler, AzeronQueryMessageHandler azeronQueryMessageHandler, AzeronServerProperties azeronServerProperties, AzeronServerNatsProperties azeronServerNatsProperties, AzeronServerInfo azeronServerInfo, InfoService infoService, ClientTracker clientTracker, TaskScheduler azeronTaskScheduler) {
        this.azeronFetchMessagePublisher = azeronFetchMessagePublisher;
        this.azeronInfoMessagePublisher = azeronInfoMessagePublisher;
        this.azeronNetworkMessageMessageHandler = azeronNetworkMessageMessageHandler;
        this.subscribeMessageHandler = subscribeMessageHandler;
        this.unsubscribeMessageHandler = unsubscribeMessageHandler;
        this.azeronSeenMessageHandler = azeronSeenMessageHandler;
        this.azeronQueryMessageHandler = azeronQueryMessageHandler;
        this.azeronServerProperties = azeronServerProperties;
        this.azeronServerNatsProperties = azeronServerNatsProperties;
        this.azeronServerInfo = azeronServerInfo;
        this.infoService = infoService;
        this.clientTracker = clientTracker;
        this.azeronTaskScheduler = azeronTaskScheduler;
    }

    @Override
    public synchronized void init(Nats nats) {
        log.trace("(re)initializing Azeron Server");
        updateInformationService();
        cancelPreviousSubs();
        setNats(nats);
        reFetchOldSubscriptions();
        fetchNetworkSubscription();
        fetchSubscriptions();
        fetchQueryHandler();
        fetchInfoSync();
        fetchChannelSync();
    }

    private void reFetchOldSubscriptions() {
        clientTracker.getChannelToClientConfigsMap().forEach((channelName, clientConfigs) -> {
            clientConfigs.forEach(clientConfig -> {
                clientTracker.removeClient(clientConfig.getServiceName());
                clientTracker.addClient(channelName, clientConfig);
            });
        });
    }

    private void updateInformationService() {
        InfoPublishDto infoPublishDto = InfoPublishDto.builder().nats(azeronServerNatsProperties).channelsCount(0).build();
        infoPublishDto.setServerUUID(azeronServerInfo.getId());
        infoService.addInfo(infoPublishDto);
    }

    private void cancelPreviousSubs() {
        log.trace("Closing all previous subscriptions");
        subscriptionList.forEach(Subscription::close);
        subscriptionList.clear();
    }

    private void fetchQueryHandler() {
        log.trace("Fetching query handler");
        Subscription subscribe = nats.subscribe(AZERON_QUERY_CHANNEL_NAME, azeronServerProperties.getQueueName(), azeronQueryMessageHandler);
        subscriptionList.add(subscribe);
    }

    public synchronized void setNats(Nats nats) {
        this.nats = nats;
    }

    private void fetchNetworkSubscription(){
        log.trace("Fetching Azeron network subscription");
        Subscription subscribe = nats.subscribe(AZERON_MAIN_CHANNEL_NAME, azeronNetworkMessageMessageHandler);
        subscriptionList.add(subscribe);
        azeronFetchMessagePublisher.publishFetchMessage(nats);
    }

    private void fetchSubscriptions(){
        log.trace("Fetching channel s/u subscription");

        Subscription subscribe = nats.subscribe(AZERON_SEEN_CHANNEL_NAME, azeronServerProperties.getQueueName(), azeronSeenMessageHandler);
        subscriptionList.add(subscribe);
        Subscription subscribe1 = nats.subscribe(AZERON_SUBSCRIBE_API_NAME, subscribeMessageHandler);
        subscriptionList.add(subscribe1);
        Subscription subscribe2 = nats.subscribe(AZERON_UNSUBSCRIBE_API_NAME, unsubscribeMessageHandler);
        subscriptionList.add(subscribe2);
    }

    private void fetchInfoSync(){
        if(this.fetchInfoSchedule != null)
            fetchInfoSchedule.cancel(true);
        PeriodicTrigger periodicTrigger = new PeriodicTrigger(azeronServerProperties.getInfoSyncIntervalSeconds(), TimeUnit.SECONDS);
        this.fetchInfoSchedule = azeronTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                azeronInfoMessagePublisher.publishInfoMessage(nats);
            }
        }, periodicTrigger);
    }

    private void fetchChannelSync(){
        if(this.fetchChannelSchedule != null)
            this.fetchChannelSchedule.cancel(true);

        if(azeronServerProperties.isShouldSyncChannels()){
            PeriodicTrigger periodicTrigger = new PeriodicTrigger(azeronServerProperties.getChannelSyncIntervalSeconds(), TimeUnit.SECONDS);
            periodicTrigger.setInitialDelay(azeronServerProperties.getChannelSyncIntervalSeconds() * 1000);
            this.fetchChannelSchedule = azeronTaskScheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    azeronFetchMessagePublisher.publishFetchMessage(nats);
                }
            }, periodicTrigger);
        }
    }
}
