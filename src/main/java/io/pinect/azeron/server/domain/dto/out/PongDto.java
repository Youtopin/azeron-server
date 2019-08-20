package io.pinect.azeron.server.domain.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.pinect.azeron.server.domain.dto.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PongDto {
    @Builder.Default
    private ResponseStatus status = ResponseStatus.OK;
    @Builder.Default
    private boolean discovered = false;
    @Builder.Default
    private boolean askedForDiscovery = false;
}
