package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ConfigChartDto extends BaseDto {
    private Long id;
    private String chartName;
    private String titleChart;

    private Long relativeTime;
    private Long unitIdView;
    private Long groupChartId;

    private String typeChart;
    private Long roleType;
    private Long timeType;

    private Long orderIndex;
    private Long status;
    private String description;

    private String chartConfig;
    private Long chartIdNextto;
    private Long chartIdDepend;

    // dto
    private List<Long> unitUsedIds;
    private Long deptId;
    private List<ConfigChartRoleDto> configChartRoleDtos;

    private Date updateTime;
    private String updateUser;
    private Long deptIdService;

    //test them truong trong DB
    private String deptIdServices;
    private List<ConfigChartItemDto> configChartItemDtos;
    private String roleCode;

    public ConfigChartDto() {
    }

    public ConfigChartDto(Long timeType) {
        this.timeType = timeType;
    }

    public ConfigChartDto(Long id, String chartName) {
        this.id = id;
        this.chartName = chartName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConfigChartDto configChartDTO = (ConfigChartDto) o;
        if (configChartDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), configChartDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
