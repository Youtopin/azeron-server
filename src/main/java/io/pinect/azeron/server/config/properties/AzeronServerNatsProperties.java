package io.pinect.azeron.server.config.properties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nats properties to be set when connecting to nats
 */
@ConfigurationProperties(prefix = "azeron.server.nats")
public class AzeronServerNatsProperties {
    private String host = "127.0.0.1";
    private String protocol="nats";
    private String port = "4222";
    private boolean useEpoll;
    private int idleTimeOut;
    private boolean pedanic;
    private int reconnectWaitSeconds;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isUseEpoll() {
        return useEpoll;
    }

    public void setUseEpoll(boolean useEpoll) {
        this.useEpoll = useEpoll;
    }

    public int getIdleTimeOut() {
        return idleTimeOut;
    }

    public void setIdleTimeOut(int idleTimeOut) {
        this.idleTimeOut = idleTimeOut;
    }

    public boolean isPedanic() {
        return pedanic;
    }

    public void setPedanic(boolean pedanic) {
        this.pedanic = pedanic;
    }

    public int getReconnectWaitSeconds() {
        return reconnectWaitSeconds;
    }

    public void setReconnectWaitSeconds(int reconnectWaitSeconds) {
        this.reconnectWaitSeconds = reconnectWaitSeconds;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
