package io.pinect.azeron.server.controller;

import io.pinect.azeron.server.config.properties.AzeronServerNatsProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/api")
@Log4j2
public class ApiController {
    private final AzeronServerNatsProperties azeronServerNatsProperties;

    @Autowired
    public ApiController(AzeronServerNatsProperties azeronServerNatsProperties) {
        this.azeronServerNatsProperties = azeronServerNatsProperties;
    }

    @GetMapping("/nats")
    public @ResponseBody AzeronServerNatsProperties getNatsDetails(HttpServletRequest httpServletRequest){
        log.trace("Nats fetch request from " + httpServletRequest.getRemoteAddr());
        return azeronServerNatsProperties;
    }

    @GetMapping("/ping")
    public @ResponseBody String ping(HttpServletRequest httpServletRequest){
        log.trace("Ping request from " + httpServletRequest.getRemoteAddr());
        return "pong";
    }
}
