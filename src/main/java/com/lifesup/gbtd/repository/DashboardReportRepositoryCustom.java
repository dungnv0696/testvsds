package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.DashboardReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DashboardReportRepositoryCustom {
    Page<DashboardReportDto> doSearch(DashboardReportDto dto, Pageable pageable);
}