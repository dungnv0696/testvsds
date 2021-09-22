package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigChartDefaultDto {
    private Long id;
    private Long timeType;
    private String typeChart;
    private String columnQuery;
    private String dataType;
    private String columnChart;
    private Long orderIndex;
    private Long status;
    private String description;
}
