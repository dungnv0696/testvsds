package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.DashboardReportDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.IDashboardReportService;
import com.lifesup.gbtd.util.PageableCustom;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard-report")
@Slf4j
public class DashboardReportController {
    private final IDashboardReportService iDashboardReportService;

    @Autowired
    public DashboardReportController(IDashboardReportService iDashboardReportService) {
        this.iDashboardReportService = iDashboardReportService;
    }

    @GetMapping("/get")
    public DataListResponse<DashboardReportDto> doSearch(DashboardReportDto dto, PageableCustom pageable) {
        return DataListResponse.success(iDashboardReportService.doSearch(dto, pageable));
    }

    @GetMapping("/{id}")
    public GenericResponse<DashboardReportDto> findById(@PathVariable Long id) {
        return GenericResponse.success(iDashboardReportService.findById(id));
    }

    @PostMapping("/update")
    public GenericResponse<DashboardReportDto> update(@Validated(value = Update.class) DashboardReportDto dto) {
        return GenericResponse.success(iDashboardReportService.update(dto));
    }

    @PostMapping("/create")
    public GenericResponse<DashboardReportDto> create(@Validated(value = Add.class) DashboardReportDto dto) {
        return GenericResponse.success(iDashboardReportService.create(dto));
    }

    @PostMapping("/lock")
    public GenericResponse<DashboardReportDto> changeFolderStatus(@RequestBody DashboardReportDto dto) {
        return GenericResponse.success(iDashboardReportService.changeFolderStatus(dto));
    }
}