package io.pinect.azeron.server.domain.dto;

import io.pinect.azeron.server.domain.model.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasicAzeronReponseDto {
    private ResponseStatus status;
}
