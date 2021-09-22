package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ServiceGBTDDefineDto extends BaseDto {
    @NotNull(groups = {Update.class})
    private Long id;
    @NotNull(message = "DeptId is required", groups = {Add.class, Update.class})
    private Long deptId;
    @NotNull(message = "ServiceId is required", groups = {Add.class, Update.class})
    private Long serviceId;
    @NotNull(message = "TimeType is required", groups = {Add.class, Update.class})
    private Long timeType;
    @NotEmpty(message = "Defination is required", groups = {Add.class, Update.class})
    private String defination;
    private Date updateTime;
    private String updateUser;
    @NotEmpty
    private String deptCode;

    private String timeTypesStr;
    private List<Long> timeTypes;

    public ServiceGBTDDefineDto(Long deptId, String defination, Date updateTime, String updateUser,
                                String timeTypesStr) {
        this.deptId = deptId;
        this.defination = defination;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.timeTypesStr = timeTypesStr;
    }

}
