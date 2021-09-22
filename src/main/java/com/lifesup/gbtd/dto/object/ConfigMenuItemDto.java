package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ConfigMenuItemDto extends BaseDto {
    private Long id;
    @NotEmpty(message = "MenuItemName is required")
    private String menuItemName;
    private Long isDefault;
    private Long orderIndex;
    private Long menuId;
    private Long chartId;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;

    private List<Long> dashboardIds;
    private Boolean haveDashboard;

    public ConfigMenuItemDto(){
        this.dashboardIds = new ArrayList<>();
    }

    public ConfigMenuItemDto(Long id, String menuItemName) {
        this.id = id;
        this.menuItemName = menuItemName;
    }
}
