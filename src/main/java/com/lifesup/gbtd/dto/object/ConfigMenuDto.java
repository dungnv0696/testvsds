package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ConfigMenuDto extends BaseDto {
    @NotNull(groups = {Update.class})
    private Long id;
    @NotEmpty(message = "MenuName is required", groups = {Add.class, Update.class})
    private String menuName;
    @NotNull(message = "Profileid is required", groups = {Add.class, Update.class})
    private Long profileId;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;
    private Long orderIndex;

    private String userRoleCode;

    public ConfigMenuDto() {
        items = new ArrayList<>();
    }

    private List<ConfigMenuItemDto> items;
    private List<Long> profileIds;

    public ConfigMenuDto(Long id, String menuName, Long profileId, Long status, String description, Date updateTime,
                         String updateUser, Long orderIndex) {
        this.id = id;
        this.menuName = menuName;
        this.profileId = profileId;
        this.status = status;
        this.description = description;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.orderIndex = orderIndex;
    }
}
