package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveDisplayQueryValueDto extends BaseDto {
    private String value;
    private String label;
    private String type;
    private String function;
}
