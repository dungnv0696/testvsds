package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.DashboardReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDashboardReportService {
    Page<DashboardReportDto> doSearch(DashboardReportDto dto, Pageable pageable);
    DashboardReportDto findById(Long id);
    DashboardReportDto create(DashboardReportDto dto);
    DashboardReportDto update(DashboardReportDto dto);
    DashboardReportDto changeFolderStatus(DashboardReportDto dto);
}