package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.DashboardParamTreeDto;

import java.util.List;

public interface DashboardParamTreeRepositoryCustom {
    List<CatDepartmentDto> doSearch(DashboardParamTreeDto dto);
    List<CatDepartmentDto> doSearchList();
}