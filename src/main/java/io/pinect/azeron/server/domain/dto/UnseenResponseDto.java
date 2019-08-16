package io.pinect.azeron.server.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.pinect.azeron.server.domain.model.ResponseStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnseenResponseDto extends BasicAzeronReponseDto{
    private boolean hasMore;
    private int count;
    private List<MessageDto> messages;

    public UnseenResponseDto() {
        super(ResponseStatus.OK);
    }
}
