package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChartDetailDto extends BaseDto {

    public ChartDetailDto() {
        data = new ArrayList<>();
        displayConfigs = new ArrayList<>();
    }
    private Long id;

    private String chartType;

    private Long orderIndex;

    private ConfigQueryChartDto query;

    private ServiceGBTDChartDto kpiInfo;
    private List<ServiceGBTDChartDto> kpiInfos;

    private List<Object> data;

    private List<ConfigDisplayQueryDto> displayConfigs;
    private List<ConfigChartDefaultDto> defaultConfigs;
}
