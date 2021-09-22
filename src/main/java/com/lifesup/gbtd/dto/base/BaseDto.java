package com.lifesup.gbtd.dto.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lifesup.gbtd.dto.object.ActionAuditDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class BaseDto implements Serializable {
    private Long totalPage;
    private Long totalRow;
    private Long page;
    private Long pageSize;
    private String keyword;
    private List<Long> ids;

    @JsonIgnore
    private ActionAuditDto.Builder logBuilder;
}
