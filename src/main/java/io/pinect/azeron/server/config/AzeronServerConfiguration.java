package io.pinect.azeron.server.config;

import io.netty.channel.nio.NioEventLoopGroup;
import io.pinect.azeron.server.AtomicNatsHolder;
import io.pinect.azeron.server.config.properties.AzeronServerNatsProperties;
import io.pinect.azeron.server.config.properties.AzeronServerProperties;
import io.pinect.azeron.server.domain.model.AzeronServerInfo;
import io.pinect.azeron.server.domain.repository.MessageRepository;
import io.pinect.azeron.server.domain.repository.VoidMessageRepository;
import io.pinect.azeron.server.service.stateListener.NatsConnectionStateListener;
import io.pinect.azeron.server.service.tracker.ClientStateListenerService;
import io.pinect.azeron.server.service.tracker.ClientTracker;
import io.pinect.azeron.server.service.tracker.InMemoryClientTracker;
import nats.client.Nats;
import nats.client.NatsConnector;
import nats.client.spring.ApplicationEventPublishingConnectionStateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAutoConfiguration
@ComponentScan("io.pinect.azeron.server")
@EnableConfigurationProperties({AzeronServerNatsProperties.class, AzeronServerProperties.class})
public class AzeronServerConfiguration {
    private final AzeronServerNatsProperties azeronServerNatsProperties;
    private final ApplicationContext applicationContext;
    private final NatsConnectionStateListener natsConnectionStateListener;
    private final ClientStateListenerService clientStateListenerService;

    @Autowired
    public AzeronServerConfiguration(AzeronServerNatsProperties azeronServerNatsProperties, ApplicationContext applicationContext, NatsConnectionStateListener natsConnectionStateListener, ClientStateListenerService clientStateListenerService) {
        this.azeronServerNatsProperties = azeronServerNatsProperties;
        this.applicationContext = applicationContext;
        this.natsConnectionStateListener = natsConnectionStateListener;
        this.clientStateListenerService = clientStateListenerService;
    }

    @Bean
    @ConditionalOnMissingBean(value = AtomicNatsHolder.class)
    public AtomicNatsHolder atomicNatsHolder(){
        NatsConnector natsConnector = new NatsConnector();
        natsConnector.addConnectionStateListener(new ApplicationEventPublishingConnectionStateListener(this.applicationContext));
        natsConnector.addConnectionStateListener(natsConnectionStateListener);
        natsConnector.addHost(azeronServerNatsProperties.getProtocol()+"://"+azeronServerNatsProperties.getHost());
        natsConnector.automaticReconnect(true);
        natsConnector.idleTimeout(azeronServerNatsProperties.getIdleTimeOut());
        natsConnector.pedantic(azeronServerNatsProperties.isPedanic());
        natsConnector.eventLoopGroup(new NioEventLoopGroup());
        natsConnector.reconnectWaitTime(5 , TimeUnit.SECONDS);
        natsConnector.eventLoopGroup(new NioEventLoopGroup());
        natsConnector.calllbackExecutor(new ScheduledThreadPoolExecutor(20));
        Nats nats = natsConnector.connect();
        return new AtomicNatsHolder(nats);
    }

    @Bean
    @ConditionalOnMissingBean(value = MessageRepository.class)
    public MessageRepository messageRepository(){
        return new VoidMessageRepository();
    }

    @Bean
    public AzeronServerInfo azeronServerInfo(){
        return new AzeronServerInfo(UUID.randomUUID().toString(), 1);
    }

    @Bean("clientTracker")
    public ClientTracker clientTracker(){
        InMemoryClientTracker inMemoryClientTracker = new InMemoryClientTracker();
        inMemoryClientTracker.addListener(clientStateListenerService);
        return inMemoryClientTracker;
    }

    @Bean("azeronTaskScheduler")
    public TaskScheduler azeronTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(20);
        threadPoolTaskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskScheduler.setRemoveOnCancelPolicy(false);
        threadPoolTaskScheduler.setThreadGroupName("azeron_server");
        threadPoolTaskScheduler.setThreadPriority(Thread.MAX_PRIORITY);
        threadPoolTaskScheduler.setBeanName("azeronTaskScheduler");
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

    @Bean("azeronExecutor")
    public Executor azeronExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setQueueCapacity(100);
        threadPoolTaskExecutor.setMaxPoolSize(100);
        threadPoolTaskExecutor.setCorePoolSize(20);
        threadPoolTaskExecutor.setDaemon(true);
        threadPoolTaskExecutor.setThreadNamePrefix("azeron_executor");
        threadPoolTaskExecutor.setBeanName("azeronExecutor");
        threadPoolTaskExecutor.setAwaitTerminationSeconds(10);
        return threadPoolTaskExecutor;
    }

}
