package io.pinect.azeron.server.config.properties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Azeron server properties
 */
@ConfigurationProperties(prefix = "azeron.server")
public class AzeronServerProperties {
    private boolean persistData;
    private boolean useCache = false;
    private boolean isDistributed = false;
    private int commitCacheSeconds = 20;
    private long cacheSizeInBytes = 128000;
    private int secondsBeforeConsiderUnAck = 5;


    public boolean isPersistData() {
        return persistData;
    }

    /**
     * @param persistData If true, azeron must persist messages when listens to them
     */
    public void setPersistData(boolean persistData) {
        this.persistData = persistData;
    }

    public boolean isUseCache() {
        return useCache;
    }

    /**
     * @param useCache If true, azeron must use its built in cache layer before committing to user implemented message repository
     */
    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public int getCommitCacheSeconds() {
        return commitCacheSeconds;
    }

    /**
     * @param commitCacheSeconds interval of seconds to commit caches to main repository
     */
    public void setCommitCacheSeconds(int commitCacheSeconds) {
        this.commitCacheSeconds = commitCacheSeconds;
    }

    public long getCacheSizeInBytes() {
        return cacheSizeInBytes;
    }

    /**
     * @param cacheSizeInBytes maximum cache size in bytes, azeron must commit all before they cache size reaches to max
     */
    public void setCacheSizeInBytes(long cacheSizeInBytes) {
        this.cacheSizeInBytes = cacheSizeInBytes;
    }

    public int getSecondsBeforeConsiderUnAck() {
        return secondsBeforeConsiderUnAck;
    }

    /**
     * @param secondsBeforeConsiderUnAck seconds to pass before Azeron considers to send an unAcknowledged message when
     *                                   sending recovery messages. This is usually maximum amount of time we think it takes
     *                                   for services to process a message before they send ack.
     */
    public void setSecondsBeforeConsiderUnAck(int secondsBeforeConsiderUnAck) {
        this.secondsBeforeConsiderUnAck = secondsBeforeConsiderUnAck;
    }

    public boolean isDistributed() {
        return isDistributed;
    }


    /**
     * In distributed environment, default cache cant handle everything and causes system problems
     * @param distributed uses Redis as distributed cache
     */
    public void setDistributed(boolean distributed) {
        this.isDistributed = distributed;
    }
}
