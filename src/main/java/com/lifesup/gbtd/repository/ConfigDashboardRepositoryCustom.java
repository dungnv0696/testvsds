package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.dto.object.ConfigDashboardDto;
import com.lifesup.gbtd.model.CatItemEntity;
import com.lifesup.gbtd.model.ConfigDashboardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConfigDashboardRepositoryCustom {
    Page<ConfigDashboardDto> doSearch(ConfigDashboardDto dto, Pageable pageable);
    List<ConfigDashboardEntity> findAll(String keyword, Long[] profileIds, Long[] menuIds, Long[] menuItemIds, Long isDefault, Long status);

    List<CatItemDto> getTimeTypeByServiceId(Long[] serviceIds);
}
