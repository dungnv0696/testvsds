package com.lifesup.gbtd.dto.request;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.validator.FieldValue;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ServiceTargetResDto extends BaseDto {
    @NotNull
    @FieldValue(numbers = {Const.SERVICE_TYPE.CT_KINHDOANH, Const.SERVICE_TYPE.CT_NGUOIDUNG})
    public Integer checkServiceType;
    @NotNull
    public Long deptId;
}
