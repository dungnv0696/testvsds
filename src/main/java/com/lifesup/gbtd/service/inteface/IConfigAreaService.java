package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ConfigAreaDto;
import com.lifesup.gbtd.dto.object.ConfigDashboardDto;

import java.util.List;

public interface IConfigAreaService {
    List<ConfigAreaDto> getByDashboardId(Long dashboardId, ConfigDashboardDto filter);
}
