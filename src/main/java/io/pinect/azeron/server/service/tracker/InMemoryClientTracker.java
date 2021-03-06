package io.pinect.azeron.server.service.tracker;

import io.pinect.azeron.server.domain.model.ClientConfig;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
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
    public synchronized boolean addClient(String channelName, ClientConfig clientConfig) {
        List<ClientConfig> clientConfigs = channelToClientConfigsMap.putIfAbsent(channelName, getClientConfigList(clientConfig));
        boolean added = true;
        if(clientConfigs != null){
            if(clientConfigs.contains(clientConfig))
                added = false;
            else
                clientConfigs.add(clientConfig);
        }

        synchronized (serviceToChannelsMap){
            List<String> channelNames = serviceToChannelsMap.get(clientConfig.getServiceName());
            if(channelNames == null){
                channelNames = new CopyOnWriteArrayList<>();
                channelNames.add(channelName);
                serviceToChannelsMap.put(clientConfig.getServiceName(), channelNames);
            }else if(!channelNames.contains(channelName)){
                channelNames.add(channelName);
            }
        }

        if(added){
            log.trace("Added new client to tracker for channel "+ channelName +" -> "+ clientConfig);
            clientStateListeners.forEach(clientStateListener -> {
                clientStateListener.onCreate(this, channelName, clientConfig);
            });
        }

        return added;
    }

    private List<ClientConfig> getClientConfigList(ClientConfig clientConfig){
        List<ClientConfig> clientConfigs = new CopyOnWriteArrayList<ClientConfig>();
        clientConfigs.add(clientConfig);
        return clientConfigs;
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


    @Override
    public void flush() {
        channelToClientConfigsMap.forEach((s, clientConfigs) -> {
            channelToClientConfigsMap.remove(s);
        });
        serviceToChannelsMap.forEach(((s, strings) -> {
            serviceToChannelsMap.remove(s);
        }));
    }
}
