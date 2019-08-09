package io.pinect.azeron.server.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AzeronServerInfo {
    private String id;
    private int version;
}
