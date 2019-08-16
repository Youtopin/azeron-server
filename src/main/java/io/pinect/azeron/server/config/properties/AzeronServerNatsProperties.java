package io.pinect.azeron.server.config.properties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nats properties to be set when connecting to nats
 */
@ConfigurationProperties(prefix = "azeron.server.nats")
@Getter
@Setter
public class AzeronServerNatsProperties {
    private String host = "localhost";
    private String hostIp = "127.0.0.1";
    private String protocol="nats";
    private String port = "4222";
    private boolean useEpoll;
    private int idleTimeOut;
    private boolean pedanic;
    private int reconnectWaitSeconds;
}
