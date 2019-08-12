package io.pinect.azeron.server.service.tracker;

import io.pinect.azeron.server.domain.model.ClientConfig;

import java.util.List;
import java.util.Map;

public interface ClientTracker {
    boolean addClient(String channelName, ClientConfig clientConfig);
    void removeClient(String serviceName);
    void removeClient(String serviceName, String channelName);
    List<ClientConfig> getClientsOfChannel(String channelName);
    List<String> getChannelsOfService(String serviceName);
    List<String> getServicesOfChannel(String channelName);
    void addListener(ClientStateListener clientStateListener);
    void removeListener(ClientStateListener clientStateListener);
    Map<String, List<ClientConfig>> getChannelToClientConfigsMap();

    interface ClientStateListener {
        void onCreate(ClientTracker clientTracker, String channelName, ClientConfig clientConfig);
        void onDelete(ClientTracker clientTracker, String serviceName, String channelName);
        void onDelete(ClientTracker clientTracker, String serviceName, List<String> channelNames);
    }
}
