package io.pinect.azeron.server.service;

import io.pinect.azeron.server.domain.dto.in.AzeronFetchRequestDto;
import io.pinect.azeron.server.domain.dto.out.AzeronChannelListDto;
import io.pinect.azeron.server.domain.model.AzeronServerInfo;
import io.pinect.azeron.server.domain.model.ClientConfig;
import io.pinect.azeron.server.service.tracker.ClientTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FetchService {
    private final ClientTracker clientTracker;
    private final AzeronServerInfo azeronServerInfo;

    @Autowired
    public FetchService(ClientTracker clientTracker, AzeronServerInfo azeronServerInfo) {
        this.clientTracker = clientTracker;
        this.azeronServerInfo = azeronServerInfo;
    }

    public AzeronChannelListDto fetch(AzeronFetchRequestDto azeronFetchRequestDto) {
        if(azeronFetchRequestDto.getServerUUID().equals(azeronServerInfo.getId()))
            return null;
        else
            return getJsonFromChannelsMap();
    }

    private AzeronChannelListDto getJsonFromChannelsMap() {
        Map<String, List<ClientConfig>> channelsToConfigsMap = clientTracker.getChannelToClientConfigsMap();
        List<AzeronChannelListDto.Channel> channels = new ArrayList<>();
        for(String channelName: channelsToConfigsMap.keySet()){
            AzeronChannelListDto.Channel channel = new AzeronChannelListDto.Channel(channelName, channelsToConfigsMap.get(channelName));
            channels.add(channel);
        }
        return new AzeronChannelListDto(azeronServerInfo.getId(), azeronServerInfo.getVersion(), channels);
    }
}
