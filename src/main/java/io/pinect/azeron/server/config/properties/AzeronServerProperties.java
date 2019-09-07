package io.pinect.azeron.server.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Azeron server properties
 */
@ConfigurationProperties(prefix = "azeron.server")
@Getter
@Setter
public class AzeronServerProperties {
    private boolean shouldSyncChannels = false;
    private int channelSyncIntervalSeconds = 20;
    private int infoSyncIntervalSeconds = 20;
    private int unseenQueryLimit = 10;
    private String queueName = "azeron-server";
}
