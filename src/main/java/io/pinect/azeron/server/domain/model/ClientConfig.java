package io.pinect.azeron.server.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ClientConfig {
    private int version;
    private String serviceName;
    private boolean persist;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientConfig)) return false;
        ClientConfig that = (ClientConfig) o;
        return getServiceName().equals(that.getServiceName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServiceName());
    }
}
