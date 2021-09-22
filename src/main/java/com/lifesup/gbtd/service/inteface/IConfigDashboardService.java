package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.dto.object.ConfigDashboardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IConfigDashboardService {
    Page<ConfigDashboardDto> doSearch(ConfigDashboardDto dto, Pageable pageable);

    ConfigDashboardDto findById(Long id);

    ConfigDashboardDto findDashboardWithFilter(Long id, ConfigDashboardDto filter);

    ConfigDashboardDto create(ConfigDashboardDto dto);

    ConfigDashboardDto update(ConfigDashboardDto dto);

    void delete(Long id);

    List<ConfigDashboardDto> findAll(String keyword, Long[] profileIds, Long[] menuIds, Long[] menuItemIds, Long isDefault, Long status);

    ConfigDashboardDto copy(Long id);

    List<CatItemDto> getTimeTypeByServiceId(Long[] serviceIds);
}
