package io.pinect.azeron.server.controller;

import io.pinect.azeron.server.config.properties.AzeronServerNatsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api")
public class ApiController {
    private final AzeronServerNatsProperties azeronServerNatsProperties;

    @Autowired
    public ApiController(AzeronServerNatsProperties azeronServerNatsProperties) {
        this.azeronServerNatsProperties = azeronServerNatsProperties;
    }

    @GetMapping("/nats")
    public @ResponseBody AzeronServerNatsProperties getNatsDetails(){
        return azeronServerNatsProperties;
    }

    @GetMapping("/ping")
    public @ResponseBody String ping(){
        return "pong";
    }
}
