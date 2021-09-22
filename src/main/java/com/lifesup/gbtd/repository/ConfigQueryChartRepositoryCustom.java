package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.ConfigQueryChartDto;

import java.util.List;

public interface ConfigQueryChartRepositoryCustom {
    List<ConfigQueryChartDto> findByChartId(Long chartId);
}
