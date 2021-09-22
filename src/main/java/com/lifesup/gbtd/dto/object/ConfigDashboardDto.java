package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.validator.FieldValue;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ConfigDashboardDto extends BaseDto {
    @NotNull(groups = Update.class)
    private Long id;
    @NotNull(groups = {Add.class, Update.class})
    private String dashboardName;
    @NotNull(groups = {Add.class, Update.class})
    private Long dashboardType;
    private Long orderIndex;
    @NotNull(groups = {Add.class, Update.class})
    private Long profileId;
    private Long menuItemId;
    private Long status;
    private String description;
    private Date updateTime;
    private String updateUser;

    //dto only
    private Long[] profileIds;
    private Long[] menuItemIds;
    private Long[] menuIds;
    @NotEmpty(groups = {Add.class, Update.class})
    private List<ConfigAreaDto> configAreaDtos;
    private ConfigMenuItemDto menuItem;
    private Long groupChartId;
    private Long timeType;
    private Long totalPageConfigArea;
    private Boolean editable;

    public ConfigDashboardDto() {
    }

    public ConfigDashboardDto(Long id) {
        this.id = id;
    }

    public ConfigDashboardDto(Long[] profileIds, Long[] menuItemIds, Long[] menuIds) {
        this.profileIds = profileIds;
        this.menuItemIds = menuItemIds;
        this.menuIds = menuIds;
    }

    public ConfigDashboardDto(Long id, String dashboardName, Long dashboardType, Long orderIndex, Long profileId,
                              Long menuItemId, Long status, String description, Date updateTime, String updateUser) {
        this.id = id;
        this.dashboardName = dashboardName;
        this.dashboardType = dashboardType;
        this.orderIndex = orderIndex;
        this.profileId = profileId;
        this.menuItemId = menuItemId;
        this.status = status;
        this.description = description;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
    }
}
