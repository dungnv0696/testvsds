package com.lifesup.gbtd.dto.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TableDto {
    @JsonProperty("Field")
    private String field;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("DisplayName")
    private String displayName;
    @JsonProperty("Comment")
    private String comment;
}
