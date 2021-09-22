package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ConfigMapChartAreaDto extends BaseDto {
    private Long id;
    private Long chartId;
    private Long areaId;
    private Long orderIndex;
    private Long dashboardIdNextto;
    private Long status;
    private Date updateTime;
    private String updateUser;

    //dto
    private String chartName;
    private String titleChart;
    private String dashboardNexttoName;
}
