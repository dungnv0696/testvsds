package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveInputParamDto extends BaseDto {
    private String paramName;
    private String fieldName;
    private String value;
    private String valueDefault;
    private String operator;
    private Boolean isFilterParam;
}
