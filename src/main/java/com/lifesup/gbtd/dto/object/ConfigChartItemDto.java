package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class ConfigChartItemDto extends BaseDto {
    private Long chartId;
    private Long id;
    private String typeChart;

    private Long queryId;
    private String inputCondition;
    private Long status;

    private Long hasAvgLine;
    private String listColor;
    private Long orderIndex;

    private String description;
    private Date updateTime;
    private String updateUser;

    //dto onlyy
    private ConfigQueryChartDto query;
//    private List<ServiceGBTDDto> kpiInfos;
//    private List<Object> data;
//    private List<ServicesMapDeptDto> kpiMapDept;
    private List<ConfigDisplayQueryDto> displayConfigs;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConfigChartItemDto configChartItemDTO = (ConfigChartItemDto) o;
        if (configChartItemDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), configChartItemDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
