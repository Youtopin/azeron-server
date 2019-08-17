package io.pinect.azeron.server.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.pinect.azeron.server.config.properties.AzeronServerNatsProperties;
import io.pinect.azeron.server.domain.dto.BasicAzeronReponseDto;
import io.pinect.azeron.server.domain.model.ResponseStatus;
import lombok.*;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
public class InfoResultDto extends BasicAzeronReponseDto {
    private List<InfoResult> results;

    public InfoResultDto(){
        super(ResponseStatus.OK);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    @Builder
    public static class InfoResult {
        private String serverUUID;
        private AzeronServerNatsProperties nats;
        private int channelsCount;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InfoResult)) return false;
            InfoResult that = (InfoResult) o;
            return Objects.equals(getServerUUID(), that.getServerUUID());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getServerUUID());
        }
    }
}
