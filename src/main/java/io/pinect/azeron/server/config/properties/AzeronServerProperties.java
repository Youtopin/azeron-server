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
    private int channelSyncIntervalSeconds = 60 * 10;
    private int infoSyncIntervalSeconds = 60 * 5;
}
