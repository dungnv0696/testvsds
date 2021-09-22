package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ConfigQueryChartDto;

import java.util.List;

public interface IConfigQueryChartService {
    List<ConfigQueryChartDto> findByIds(List<Long> ids);
}
