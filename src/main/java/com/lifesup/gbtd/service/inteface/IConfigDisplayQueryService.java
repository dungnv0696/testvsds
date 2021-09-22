package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ConfigDisplayQueryDto;

import java.util.List;

public interface IConfigDisplayQueryService {
    List<ConfigDisplayQueryDto> findByChartItemIds(List<Long> chartItemIds);
}
