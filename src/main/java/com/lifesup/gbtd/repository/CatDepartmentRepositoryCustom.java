package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.ParamTreeDto;

import java.util.List;

public interface CatDepartmentRepositoryCustom {
    List<CatDepartmentDto> getDepartmentTreeByDeptId2(Long deptId, List<Long> deptLevel,String typeParam);
    List<CatDepartmentDto> getDepartmentTreeByDeptId(Long deptId, List<Long> deptLevel);
    List<CatDepartmentDto> getParamTreeDept(ParamTreeDto obj);
    List<CatDepartmentDto> getDepartmentTreeByDeptLevelAndName(CatDepartmentDto dto);
}
