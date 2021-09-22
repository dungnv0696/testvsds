package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.CatGroupChartDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICatGroupChartService {
    Page<CatGroupChartDto> findAllCatGroups(Long dashboardId, Long status, Pageable pageable);
    Page<CatGroupChartDto> findAllCatGroups(CatGroupChartDto dto, Pageable pageable);
}
