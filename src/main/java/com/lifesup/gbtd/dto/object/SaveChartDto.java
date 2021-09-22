package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SaveChartDto extends ConfigChartDto {

    public SaveChartDto() {
        items = new ArrayList<>();
    }

    public SaveChartDto(ConfigChartDto config) {
        this.setId(config.getId());
        this.setChartName(config.getChartName());
        this.setTitleChart(config.getTitleChart());
        this.setTypeChart(config.getTypeChart());
        this.setRoleType(config.getRoleType());
        this.setTimeType(config.getTimeType());
        this.setRelativeTime(config.getRelativeTime());
        this.setUnitIdView(config.getUnitIdView());
        this.setGroupChartId(config.getGroupChartId());
        this.setChartConfig(config.getChartConfig());
        this.setChartIdNextto(config.getChartIdNextto());
        this.setChartIdDepend(config.getChartIdDepend());
        this.setOrderIndex(config.getOrderIndex());
        this.setStatus(config.getStatus());
        this.setDescription(config.getDescription());
        this.setUpdateTime(config.getUpdateTime());
        this.setUpdateUser(config.getUpdateUser());
        this.setDeptIdService(config.getDeptIdService());
        this.setDeptIdServices(config.getDeptIdServices());
    }

    private ConfigChartDto chartNextto;
    private List<SaveChartItemDto> items;
}
