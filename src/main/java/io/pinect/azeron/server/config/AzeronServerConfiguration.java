package io.pinect.azeron.server.config;

import io.pinect.azeron.server.AtomicNatsHolder;
import io.pinect.azeron.server.config.properties.AzeronServerNatsProperties;
import io.pinect.azeron.server.config.properties.AzeronServerProperties;
import io.pinect.azeron.server.domain.model.AzeronServerInfo;
import io.pinect.azeron.server.domain.repository.MessageRepository;
import io.pinect.azeron.server.domain.repository.VoidMessageRepository;
import io.pinect.azeron.server.service.stateListener.NatsConnectionStateListener;
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

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAutoConfiguration
@ComponentScan("io.pinect.azeron.server")
@EnableConfigurationProperties({AzeronServerNatsProperties.class, AzeronServerProperties.class})
public class AzeronServerConfiguration {
    private final AzeronServerNatsProperties azeronServerNatsProperties;
    private final ApplicationContext applicationContext;
    private final NatsConnectionStateListener natsConnectionStateListener;

    @Autowired
    public AzeronServerConfiguration(AzeronServerNatsProperties azeronServerNatsProperties, ApplicationContext applicationContext, NatsConnectionStateListener natsConnectionStateListener) {
        this.azeronServerNatsProperties = azeronServerNatsProperties;
        this.applicationContext = applicationContext;
        this.natsConnectionStateListener = natsConnectionStateListener;
    }

    @Bean
    @ConditionalOnMissingBean(value = AtomicNatsHolder.class)
    public AtomicNatsHolder atomicNatsHolder(){
        NatsConnector natsConnector = new NatsConnector();
        natsConnector.addConnectionStateListener(new ApplicationEventPublishingConnectionStateListener(this.applicationContext));
        natsConnector.addConnectionStateListener(natsConnectionStateListener);
        natsConnector.addHost(azeronServerNatsProperties.getHost());
        natsConnector.automaticReconnect(true);
        natsConnector.idleTimeout(azeronServerNatsProperties.getIdleTimeOut());
        natsConnector.pedantic(azeronServerNatsProperties.isPedanic());
        natsConnector.reconnectWaitTime(5 , TimeUnit.SECONDS);
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

}
