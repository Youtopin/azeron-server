package io.pinect.azeron.server.controller;

import io.pinect.azeron.server.config.properties.AzeronServerNatsProperties;
import io.pinect.azeron.server.domain.dto.ResponseStatus;
import io.pinect.azeron.server.domain.dto.in.SeenDto;
import io.pinect.azeron.server.domain.dto.out.ClientInfoDto;
import io.pinect.azeron.server.domain.dto.out.InfoResultDto;
import io.pinect.azeron.server.domain.dto.out.PongDto;
import io.pinect.azeron.server.domain.dto.out.SeenResponseDto;
import io.pinect.azeron.server.service.InfoService;
import io.pinect.azeron.server.service.SeenService;
import io.pinect.azeron.server.service.tracker.ClientTracker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Log4j2
public class ApiController {
    private final AzeronServerNatsProperties azeronServerNatsProperties;
    private final SeenService seenService;
    private final ClientTracker clientTracker;
    private final InfoService infoService;

    @Autowired
    public ApiController(AzeronServerNatsProperties azeronServerNatsProperties, SeenService seenService, ClientTracker clientTracker, InfoService infoService) {
        this.azeronServerNatsProperties = azeronServerNatsProperties;
        this.seenService = seenService;
        this.clientTracker = clientTracker;
        this.infoService = infoService;
    }

    @GetMapping("/listeners")
    public @ResponseBody
    ClientInfoDto.ListenersDto getListeners(){
        List<ClientInfoDto> clientInfoDtos = new ArrayList<>();
        clientTracker.getChannelToClientConfigsMap().forEach((channel, clientConfigs) -> {
            clientConfigs.forEach(clientConfig -> {
                clientInfoDtos.add(ClientInfoDto.builder().channel(channel).serviceName(clientConfig.getServiceName()).build());
            });
        });

        return new ClientInfoDto.ListenersDto(clientInfoDtos, null);
    }


    @GetMapping("/info")
    public @ResponseBody
    InfoResultDto getServersInfo(HttpServletRequest httpServletRequest){
        log.trace("Info request from " + httpServletRequest.getRemoteAddr());
        return infoService.getInfoResultDto();
    }

    @GetMapping("/nats")
    public @ResponseBody AzeronServerNatsProperties getNatsDetails(HttpServletRequest httpServletRequest){
        log.trace("Nats fetch request from " + httpServletRequest.getRemoteAddr());
        return azeronServerNatsProperties;
    }

    @GetMapping("/ping")
    public @ResponseBody
    PongDto ping(@RequestParam(value = "serviceName", required = false) String serviceName, HttpServletRequest httpServletRequest){
        log.trace("Ping request from ADDRESS: " + httpServletRequest.getRemoteAddr() + " - SERVICE: "+ serviceName);
        if(serviceName == null)
            return new PongDto();
        else {
            List<String> channelsOfService = clientTracker.getChannelsOfService(serviceName);
            return PongDto.builder().askedForDiscovery(true).discovered(channelsOfService != null && channelsOfService.size() > 0).status(ResponseStatus.OK).build();
        }
    }

    @PutMapping("/seen")
    public @ResponseBody
    SeenResponseDto seenMessages(@Valid @RequestBody SeenDto seenDto){
        return seenService.seen(seenDto);
    }
}
