package io.pinect.azeron.server.service.tracker;

import io.pinect.azeron.server.domain.model.ClientConfig;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
public class InMemoryClientTracker implements ClientTracker {
    private final List<ClientStateListener> clientStateListeners = new CopyOnWriteArrayList<>();
    private final Map<String, List<ClientConfig>> channelToClientConfigsMap = new ConcurrentHashMap<>();
    private final Map<String, List<String>> serviceToChannelsMap = new ConcurrentHashMap<>();

    @Override
    public boolean addClient(String channelName, ClientConfig clientConfig) {
        List<ClientConfig> clientConfigs = channelToClientConfigsMap.putIfAbsent(channelName, Collections.singletonList(clientConfig));
        boolean added = true;
        if(clientConfigs != null && !clientConfigs.contains(clientConfig)){
            clientConfigs.add(clientConfig);
        }else {
            added = false;
        }

        List<String> channelNames = serviceToChannelsMap.putIfAbsent(clientConfig.getServiceName(), Collections.singletonList(channelName));
        if(channelNames != null && !channelNames.contains(channelName))
            channelNames.add(channelName);

        if(added){
            log.trace("Added new client to tracker -> "+ clientConfig);
            clientStateListeners.forEach(clientStateListener -> {
                clientStateListener.onCreate(this, channelName, clientConfig);
            });
        }

        return added;
    }

    @Override
    public void removeClient(String serviceName) {
        log.trace("Removing client from tracker. service name -> "+ serviceName);

        List<String> channelNames = serviceToChannelsMap.remove(serviceName);
        channelNames.forEach(channelToClientConfigsMap::remove);

        clientStateListeners.forEach(clientStateListener -> {
            clientStateListener.onDelete(this, serviceName, channelNames);
        });
    }

    @Override
    public synchronized void removeClient(String serviceName, String channelName) {
        log.trace("Removing client from tracker. service name -> "+ serviceName + " , channel name -> "+ channelName);

        if(channelName == null){
            removeClient(serviceName);
            return;
        }
        List<String> channelNames = serviceToChannelsMap.get(serviceName);
        channelNames.remove(channelName);

        if(channelNames.size() == 0){
            serviceToChannelsMap.remove(serviceName);
        }

        List<ClientConfig> clientConfigs = channelToClientConfigsMap.get(channelName);

        for(ClientConfig clientConfig: clientConfigs){
            if(clientConfig.getServiceName().equals(serviceName)){
                clientConfigs.remove(clientConfig);
                break;
            }
        }

        if(clientConfigs.size() == 0){
            channelToClientConfigsMap.remove(channelName);
        }

        clientStateListeners.forEach(clientStateListener -> {
            clientStateListener.onDelete(this, serviceName, channelName);
        });
    }

    @Override
    public List<ClientConfig> getClientsOfChannel(String channelName) {
        return channelToClientConfigsMap.getOrDefault(channelName, new ArrayList<>());
    }

    @Override
    public List<String> getChannelsOfService(String serviceName) {
        return serviceToChannelsMap.getOrDefault(serviceName, new ArrayList<>());
    }

    @Override
    public List<String> getServicesOfChannel(String channelName) {
        List<ClientConfig> clientConfigs = channelToClientConfigsMap.getOrDefault(channelName, new ArrayList<>());
        List<String> result = new ArrayList<>();
        clientConfigs.forEach(clientConfig -> {
            result.add(clientConfig.getServiceName());
        });
        return result;
    }

    @Override
    public void addListener(ClientStateListener clientStateListener) {
        clientStateListeners.add(clientStateListener);
    }

    @Override
    public void removeListener(ClientStateListener clientStateListener) {
        clientStateListeners.remove(clientStateListener);
    }

    @Override
    public Map<String, List<ClientConfig>> getChannelToClientConfigsMap() {
        return channelToClientConfigsMap;
    }
}
