package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class ConfigProfileRoleDto extends BaseDto {
    @NotNull
    private Long id;
    @NotNull
    private Long profileId;
    @NotNull
    private Long deptId;
    @NotEmpty
    private String usernameUsed;
    @NotEmpty
    private String roleCode;
    private Date updateTime;
    private String updateUser;

    private Long roleType;
}
