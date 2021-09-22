package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.DashboardParamTreeDto;
import com.lifesup.gbtd.dto.object.SaveDashboardParamTreeDto;

import java.util.List;

public interface IDashboardParamTreeService {
    List<DashboardParamTreeDto> update(SaveDashboardParamTreeDto dto);
    void delete(DashboardParamTreeDto dto);
    List<DashboardParamTreeDto> dashboardParamTreeByParentDeptId(DashboardParamTreeDto dto);
    List<CatDepartmentDto> doSearch(DashboardParamTreeDto dto);
    List<CatDepartmentDto> getListDashboardParam();
}