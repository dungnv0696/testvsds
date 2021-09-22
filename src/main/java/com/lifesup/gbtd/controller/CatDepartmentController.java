package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.ParamTreeDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.ICatDepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/cat-department")
@Slf4j
public class CatDepartmentController {

    private final ICatDepartmentService catDepartmentService;

    @Autowired
    public CatDepartmentController(ICatDepartmentService catDepartmentService) {
        this.catDepartmentService = catDepartmentService;
    }

    @GetMapping("/get-for-combo")
    public DataListResponse<CatDepartmentDto> getForCombo(Long[] inputLevel, String fullTree) {
        DataListResponse<CatDepartmentDto> res = new DataListResponse<>();
        res.setData(catDepartmentService.getDepartmentByDeptLevel(inputLevel, fullTree));
//        res.toString();
//       Long data= res.getData().get(1).getParent();
//        System.out.println(data);
        return res;
    }

    @GetMapping("/get-for-combo-dvct")
    public DataListResponse<CatDepartmentDto> getForComboDVCT() {
        DataListResponse<CatDepartmentDto> res = new DataListResponse<>();
        res.setData(catDepartmentService.getDepartmentsForDVCT());
        return res;
    }

    @GetMapping("/param-tree")
    public DataListResponse<CatDepartmentDto> getParamTreeDept(ParamTreeDto dto) {
        return DataListResponse.success(catDepartmentService.getParamTreeDept(dto));
    }

    @GetMapping("/get-all")
    public DataListResponse<CatDepartmentDto> doSearch(CatDepartmentDto departmentDto) {
        return DataListResponse.success(catDepartmentService.doSearch(departmentDto));
    }

    @PostMapping("/create")
    public GenericResponse<CatDepartmentDto> createCatDepartment(@Valid @RequestBody CatDepartmentDto catDepartmentDto) {
        return GenericResponse.success(catDepartmentService.createCatDepartment(catDepartmentDto));
    }

    @PostMapping("/update")
    public GenericResponse<CatDepartmentDto> updateCatDepartment(@Valid @RequestBody CatDepartmentDto catDepartmentDto) {
        return GenericResponse.success(catDepartmentService.updateCatDepartment(catDepartmentDto));
    }

    @PostMapping("/delete")
    public GenericResponse delete(@RequestBody CatDepartmentDto dto) {
        GenericResponse res = new GenericResponse<>();
        catDepartmentService.delete(dto);
        res.success();
        return res;
    }

    @GetMapping("/{id}")
    public GenericResponse<CatDepartmentDto> findById(@PathVariable Long id) {
        return GenericResponse.success(catDepartmentService.findById(id));
    }
}
