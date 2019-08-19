package io.pinect.azeron.server.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)

@Getter
@Setter
@Builder
public class MessageDto implements Serializable {
    private String messageId;
    private String channelName;
    private long timeStamp;
    private JsonNode object;
    private String serviceName;
}