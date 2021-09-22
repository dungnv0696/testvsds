package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ConfigChartItemDto;

import java.util.List;

public interface IConfigChartItemService {
    List<ConfigChartItemDto> findByChartId(Long chartId);
}
