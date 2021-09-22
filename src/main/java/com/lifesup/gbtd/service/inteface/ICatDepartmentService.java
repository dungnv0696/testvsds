package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.ParamTreeDto;

import java.util.List;

public interface ICatDepartmentService {
    List<CatDepartmentDto> getDepartmentsCurrentUser(List<Long> deptLevels);

    List<CatDepartmentDto> getDeptTreeByDeptId2(Long deptId, List<Long> deptLevels,String typeParam);
    List<CatDepartmentDto> getDeptTreeByDeptId(Long deptId, List<Long> deptLevels);

    List<CatDepartmentDto> getDepartmentByDeptLevel(Long[] inputLevel, String fullTree);

    List<CatDepartmentDto> getDepartmentsForDVCT();

    List<CatDepartmentDto> getParamTreeDept(ParamTreeDto obj);

    List<CatDepartmentDto> doSearch(CatDepartmentDto departmentDto);

    CatDepartmentDto createCatDepartment(CatDepartmentDto catDepartmentDto);

    CatDepartmentDto updateCatDepartment(CatDepartmentDto catDepartmentDto);

    void delete(CatDepartmentDto dto);

    CatDepartmentDto findById(Long id);
}
