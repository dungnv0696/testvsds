package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.CatGroupChartDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CatGroupChartRepositoryCustom {
    Page<CatGroupChartDto> findAllCatGroups(Long dashboardId, Long status, Pageable pageable);
    Page<CatGroupChartDto> findAllCatGroups(CatGroupChartDto dto, Pageable pageable);
}
