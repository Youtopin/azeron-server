package io.pinect.azeron.server.domain.dto.in;

import lombok.Data;

@Data
public class UnseenQueryDto {
    private String serviceName;
    private long dateBefore;
}
