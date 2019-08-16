package io.pinect.azeron.server.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.pinect.azeron.server.domain.model.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)

public class SeenResponseDto extends BasicAzeronReponseDto{
    private String reqId;

    public SeenResponseDto(ResponseStatus status, String reqId) {
        super(status);
        this.reqId = reqId;
    }
}
