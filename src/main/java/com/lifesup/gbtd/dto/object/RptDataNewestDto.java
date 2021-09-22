package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RptDataNewestDto extends BaseDto {
    private Long id;
    private Long serviceId;
    private Long inputLevel;
    private Long timeType;
    private Long prdId;
}
