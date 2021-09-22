package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParamsReportDto extends BaseDto {
    private String paramName;
    private String promptText;
    private boolean isHidden;
    private String value;
    private String dataType;
    private List<Object> selectionList;
    private String displayFormat;
    private Object defaultValue;
}
