package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.DashboardParamTreeDto;
import com.lifesup.gbtd.dto.object.SaveDashboardParamTreeDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.IDashboardParamTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/dashboard-param-tree")
@Slf4j
public class DashboardParamTreeController {
    private final IDashboardParamTreeService dashboardParamTreeService;

    @Autowired
    public DashboardParamTreeController(IDashboardParamTreeService dashboardParamTreeService) {
        this.dashboardParamTreeService = dashboardParamTreeService;
    }

    @PostMapping("/update")
    public DataListResponse<DashboardParamTreeDto> updateDashboardParamTree(@Valid @RequestBody SaveDashboardParamTreeDto dto) {
        return DataListResponse.success(dashboardParamTreeService.update(dto));
    }

    @PostMapping("/delete")
    public GenericResponse delete(@RequestBody DashboardParamTreeDto dto) {
        GenericResponse res = new GenericResponse<>();
        dashboardParamTreeService.delete(dto);
        res.success();
        return res;
    }

    @GetMapping("/get")
    public DataListResponse<DashboardParamTreeDto> get(DashboardParamTreeDto dashboardParamTreeDto) {
        return DataListResponse.success(dashboardParamTreeService.dashboardParamTreeByParentDeptId(dashboardParamTreeDto));
    }

    @GetMapping("/get-all")
    public DataListResponse<CatDepartmentDto> getAll(DashboardParamTreeDto dashboardParamTreeDto) {
        return DataListResponse.success(dashboardParamTreeService.doSearch(dashboardParamTreeDto));
    }

    @GetMapping("/get-list")
    public DataListResponse<CatDepartmentDto> getListDashboardParam() {
        return DataListResponse.success(dashboardParamTreeService.getListDashboardParam());
    }

}
