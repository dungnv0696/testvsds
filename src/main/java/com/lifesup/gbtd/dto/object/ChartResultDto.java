package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ChartResultDto {
    // config chart properties
    private Long id;
    private String chartName;
    private String titleChart;

    private Long orderIndex;
    private Long status;
    private String description;
    private String chartConfig;
    private Long chartIdNextto;
    private Long chartIdDepend;

    private Long relativeTime;
    private Long unitIdView;
    private Long groupChartId;
    private String typeChart;
    private Long roleType;
    private Long timeType;

    private List<ConfigChartRoleDto> configChartRole;
    private Map<String, Object> filterParams;
    private List<ChartDetailDto> details;

    private String deptCodeService;

    private Date updateTime;
    private String updateUser;
    private Long deptIdService;

    public ChartResultDto() {
        details = new ArrayList<>();
    }

    public ChartResultDto(ConfigChartDto config) {
        this.setId(config.getId());
        this.setChartName(config.getChartName());
        this.setTitleChart(config.getTitleChart());

        this.setRelativeTime(config.getRelativeTime());
        this.setUnitIdView(config.getUnitIdView());
        this.setGroupChartId(config.getGroupChartId());
        this.setTypeChart(config.getTypeChart());
        this.setRoleType(config.getRoleType());
        this.setTimeType(config.getTimeType());

        this.setOrderIndex(config.getOrderIndex());
        this.setStatus(config.getStatus());
        this.setDescription(config.getDescription());

        this.setChartConfig(config.getChartConfig());
        this.setChartIdNextto(config.getChartIdNextto());
        this.setChartIdDepend(config.getChartIdDepend());

        this.setUpdateTime(config.getUpdateTime());
        this.setUpdateUser(config.getUpdateUser());
        this.setDeptIdService(config.getDeptIdService());
    }
}
