package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ConfigAreaDto extends BaseDto {
    private Long id;
    private String areaName;
    private Long orderIndex;

    private String description;
    private Date updateTime;
    private String updateUser;

    private String positionJson;
    private Long dashboardId;
    private Long timeRefresh;

    private Long pageDashboard;

    // dto
    private List<ConfigMapChartAreaDto> mapCharts;

    public ConfigAreaDto() {
        this.mapCharts = new ArrayList<>();
    }
}
