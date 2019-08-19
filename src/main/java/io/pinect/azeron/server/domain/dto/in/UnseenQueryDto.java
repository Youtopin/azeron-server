package io.pinect.azeron.server.domain.dto.in;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UnseenQueryDto {
    private String serviceName;
    private long dateBefore;
}
