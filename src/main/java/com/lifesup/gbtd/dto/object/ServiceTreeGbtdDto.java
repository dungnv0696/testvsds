package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class ServiceTreeGbtdDto extends BaseDto {
    @NotNull(groups = {Update.class})
    private Long id;
    @NotNull(groups = {Add.class, Update.class})
    private Long deptId;
    @NotNull(groups = {Add.class, Update.class})
    private Long serviceId;
    @NotNull(groups = {Add.class, Update.class})
    private Long parentServiceId;
    @NotNull(groups = {Add.class})
    private Long rate;
    private Long status;
    private String description;
    @NotNull(groups = {Add.class})
    private String source;
    private Date updateTime;
    private String updateUser;

    private String serviceName;
}
