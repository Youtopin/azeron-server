package io.pinect.azeron.server.domain.dto.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SeenDto {
    private String messageId;
    private List<String> messageIds;
    @NotNull
    private String serviceName;
    private String reqId;

    @JsonIgnore
    @AssertTrue
    public boolean isValidMessage(){
        return messageId != null || messageIds != null;
    }
}
