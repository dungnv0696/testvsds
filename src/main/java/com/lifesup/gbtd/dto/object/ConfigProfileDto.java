package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ConfigProfileDto extends BaseDto {

    @NotNull(groups = {Update.class})
    private Long id;
    @NotNull(message = "ProfileName is required", groups = {Update.class, Add.class})
    private String profileName;
    @NotNull(message = "IsDefault is required", groups = {Update.class, Add.class})
    private Long isDefault;
    private Long orderIndex;
    @NotNull(message = "Status is required", groups = {Update.class, Add.class})
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;
    @NotNull(message = "RoleType is required", groups = {Update.class, Add.class})
    private Long roleType;

    private String usernameUsed;
    private String roleCode;
    private String userRoleCode;
    private Long deptId;
    private Long hasChild;
    private String action;
    public ConfigProfileDto(Long id, String profileName, Long isDefault, Long orderIndex, Long roleType,
                            Long status, String description, Date updateTime, String updateUser, Long hasChild, String roleCode) {
        this.id = id;
        this.profileName = profileName;
        this.isDefault = isDefault;
        this.orderIndex = orderIndex;
        this.roleType = roleType;
        this.status = status;
        this.description = description;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.hasChild = hasChild;
        this.roleCode = roleCode;
    }

    public ConfigProfileDto(Long id, String profileName, Long isDefault, Long orderIndex, Long roleType,
                            Long status, String description, Date updateTime, String updateUser) {
        this.id = id;
        this.profileName = profileName;
        this.isDefault = isDefault;
        this.orderIndex = orderIndex;
        this.roleType = roleType;
        this.status = status;
        this.description = description;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
    }

    public ConfigProfileDto(Long id, String profileName, Long isDefault, Long orderIndex, Long roleType,
                            Long status, String description, Date updateTime, String updateUser, String roleCode) {
        this.id = id;
        this.profileName = profileName;
        this.isDefault = isDefault;
        this.orderIndex = orderIndex;
        this.roleType = roleType;
        this.status = status;
        this.description = description;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.roleCode = roleCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConfigProfileDto configProfileDTO = (ConfigProfileDto) o;
        if (configProfileDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), configProfileDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

//    @Override
//    public String toString() {
//        return "ConfigProfileDTO{" +
//            "id=" + getId() +
//            ", profileCode='" + getProfileCode() + "'" +
//            ", profileName='" + getProfileName() + "'" +
//            ", isDefault=" + getIsDefault() +
//            ", orderIndex=" + getOrderIndex() +
//            ", roleCode='" + getRoleCode() + "'" +
//            ", status=" + getStatus() +
//            ", description='" + getDescription() + "'" +
//            ", updateTime='" + getUpdateTime() + "'" +
//            ", updateUser='" + getUpdateUser() + "'" +
//            "}";
//    }
}
