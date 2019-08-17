package io.pinect.azeron.server.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UnSubscribeControlDto {
    private String channelName;
    private String serviceName;
}
