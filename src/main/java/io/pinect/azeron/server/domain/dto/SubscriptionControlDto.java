package io.pinect.azeron.server.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.pinect.azeron.server.domain.model.ClientConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SubscriptionControlDto {
    private String channelName;
    private ClientConfig config;
}
