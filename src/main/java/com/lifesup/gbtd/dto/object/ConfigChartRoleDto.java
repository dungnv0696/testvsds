package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ConfigChartRoleDto extends BaseDto {
    private Long id;
    private Long chartId;
    private Long deptId;
    private String usernameUsed;
    private String roleCode;
    private Date updateTime;
    private String updateUser;

    private Long roleType;
}
