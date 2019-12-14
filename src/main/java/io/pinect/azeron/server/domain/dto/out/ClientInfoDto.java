package io.pinect.azeron.server.domain.dto.out;

import io.pinect.azeron.server.domain.dto.BasicAzeronReponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class ClientInfoDto {
    private String serviceName;
    private String channel;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListenersDto extends BasicAzeronReponseDto {
        List<ClientInfoDto> listeners;
        ClientInfoDto listener;
    }
}
