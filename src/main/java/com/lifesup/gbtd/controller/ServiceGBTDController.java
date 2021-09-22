package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.BiTdServicesTreeDto;
import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
import com.lifesup.gbtd.dto.object.ServiceTreeGbtdDto;
import com.lifesup.gbtd.dto.object.ServicesMapDeptDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.CatDepartmentService;
import com.lifesup.gbtd.service.CatItemService;
import com.lifesup.gbtd.service.CatUnitService;
import com.lifesup.gbtd.service.inteface.IServiceGBTDService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/serviceGbtd")
@Slf4j
public class ServiceGBTDController {
    @Autowired
    public ServiceGBTDController(IServiceGBTDService serviceGBTDService, CatItemService catItemService,
                                 CatDepartmentService catDepartmentService, CatUnitService catUnitService) {
        this.serviceGBTDService = serviceGBTDService;
        this.catItemService = catItemService;
        this.catDepartmentService = catDepartmentService;
        this.catUnitService = catUnitService;
    }

    private final IServiceGBTDService serviceGBTDService;
    private final CatItemService catItemService;
    private final CatDepartmentService catDepartmentService;
    private final CatUnitService catUnitService;

    @GetMapping("/loadDefault")
    public GenericResponse<HashMap> getAllValueDefault() {
        GenericResponse<HashMap> res = new GenericResponse<>();
        HashMap<String, List> data = new HashMap<>();

        data.put("targetTypesSearch", catItemService.findByCategoryCode("SERVICE_TYPE"));
        data.put("targetGroupsSearch", catItemService.findByCategoryCode("GROUP_KPI"));
        data.put("targetTypes", catItemService.findByCategoryCodeAndStatus("SERVICE_TYPE", Const.STATUS.ACTIVE));
        data.put("targetGroups", catItemService.findByCategoryCodeAndStatus("GROUP_KPI", Const.STATUS.ACTIVE));
        // TO DO current user
        data.put("departments", catDepartmentService.getDepartmentsCurrentUser(Arrays.asList(
                Const.DEPT_LEVEL.TAP_DOAN,
                Const.DEPT_LEVEL.TCT_CTY_PHB,
                Const.DEPT_LEVEL.THI_TRUONG,
                Const.DEPT_LEVEL.NHOM_DON_VI
        )));
        data.put("catUnits", catUnitService.findByStatus(Const.STATUS.ACTIVE));
        res.setData(data);
        res.success();
        return res;
    }

    @PostMapping("/findByParams")
    public GenericResponse<HashMap> findByParams(@RequestBody ServiceGBTDDto holder) {
        GenericResponse<HashMap> res = new GenericResponse<>();
        HashMap<String, List> data = new HashMap<>();
        List<ServiceGBTDDto> dtos = serviceGBTDService.findServiceGBTDs(holder);
        data.put("serviceGBTDs", dtos);
        res.setData(data);
        res.setPaging(holder);
        res.success();
        return res;
    }

    @PostMapping("/add")
    public GenericResponse<ServiceGBTDDto> addServiceGBTD(@Validated(value = Add.class)
                                                          @RequestBody ServiceGBTDDto serviceGBTDDto) {
        GenericResponse<ServiceGBTDDto> res = new GenericResponse<>();
        serviceGBTDService.add(serviceGBTDDto);
        res.success();
        return res;
    }

    @PostMapping("/update")
    public GenericResponse<ServiceGBTDDto> updateServiceGBTD(@Validated(value = Update.class)
                                                             @RequestBody ServiceGBTDDto serviceGBTDDto) {
        GenericResponse<ServiceGBTDDto> res = new GenericResponse<>();
        serviceGBTDService.update(serviceGBTDDto);
        res.success();
        return res;
    }

    @PostMapping("/delete")
    public GenericResponse<Boolean> deleteById(@RequestBody ServiceGBTDDto dto) {
        GenericResponse<Boolean> res = new GenericResponse<>();
        serviceGBTDService.delete(dto.getServiceId());
        res.success();
        return res;
    }

    @PostMapping("/findById")
    public GenericResponse<ServiceGBTDDto> findById(@RequestBody ServiceGBTDDto dto) {
        GenericResponse<ServiceGBTDDto> res = new GenericResponse<>();
        res.setData(serviceGBTDService.findById(dto.getId()));
        res.success();
        return res;
    }

    @PostMapping("/have-formula-define")
    public GenericResponse<String> haveFormulaAndDefine(@RequestBody ServiceGBTDDto dto) {
        return GenericResponse.success(serviceGBTDService.haveFormulaOrDefine(dto));
    }

    // --------------- cong thuc chi tieu ----------------
    @PostMapping("/formula/serviceSource")
    public GenericResponse<List<String>> findServiceSourceByDeptCode(@NotNull @RequestBody CatItemDto dto) {
        GenericResponse<List<String>> res = new GenericResponse<>();
        res.setData(catItemService.findServiceSourceByDeptCode(dto.getItemCode()));
        res.success();
        return res;
    }

    @PostMapping("/formula/doSearch")
    public GenericResponse<List<BiTdServicesTreeDto>> getListServiceFormula(@NotNull @RequestBody ServiceGBTDDto dto) {
        GenericResponse<List<BiTdServicesTreeDto>> res = new GenericResponse<>();
        res.setData(serviceGBTDService.getListServiceFormula(dto));
        res.setPaging(dto);
        res.success();
        return res;
    }

    @GetMapping("/formula/services")
    public GenericResponse<List<ServiceGBTDDto>> getAllServiceGbtd() {
        GenericResponse<List<ServiceGBTDDto>> res = new GenericResponse<>();
        res.setData(serviceGBTDService.getAllServiceGbtd());
        res.success();
        return res;
    }

    @PostMapping("/formula/add")
    public GenericResponse addFormula(@Validated(value = Add.class) @RequestBody List<BiTdServicesTreeDto> dtos) {
        GenericResponse res = new GenericResponse<>();
        serviceGBTDService.saveServiceFormula(dtos);
        res.success();
        return res;
    }

    @PostMapping("/formula/update")
    public GenericResponse updateFormula(@NotNull @RequestBody List<@Valid BiTdServicesTreeDto> dtos) {
        GenericResponse res = new GenericResponse<>();
        serviceGBTDService.updateServiceFormula(dtos);
        res.success();
        return res;
    }

    @PostMapping("/formula/delete")
    public GenericResponse deleteFormula(@NotNull @RequestBody BiTdServicesTreeDto dto) {
        GenericResponse res = new GenericResponse<>();
        serviceGBTDService.deleteServiceFormula(dto);
        res.success();
        return res;
    }

    @PostMapping("/formula/services")
    public GenericResponse<List<ServiceGBTDDto>> findServicesByDeptId(@NotNull @RequestBody ServiceGBTDDto dto) {
        GenericResponse<List<ServiceGBTDDto>> res = new GenericResponse<>();
        res.setData(serviceGBTDService.findServicesByDeptId(dto.getDeptId(), dto.getServiceId()));
        res.success();
        return res;
    }

    @GetMapping
    public DataListResponse<ServiceGBTDDto> findServiceOfDept(ServiceGBTDDto dto) {
        return DataListResponse.success(serviceGBTDService.findServiceOfDept(dto));
    }
//
//    @GetMapping("/component")
//    public DataListResponse<ServiceGBTDDto> findChildrenService(ServiceGBTDDto dto) {
//        return DataListResponse.success(serviceGBTDService.findChildrenService(dto));
//    }
}
