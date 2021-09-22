package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.ServiceGBTDDefineDto;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.ICatDepartmentService;
import com.lifesup.gbtd.service.inteface.ICatItemService;
import com.lifesup.gbtd.service.inteface.IServiceGBTDDefine;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.validator.group.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/serviceDefine")
@Slf4j
public class ServiceGBTDDefineController {

    public ServiceGBTDDefineController(ICatItemService catItemService, ICatDepartmentService catDepartmentService,
                                       IServiceGBTDDefine serviceGBTDDefine) {
        this.catDepartmentService = catDepartmentService;
        this.catItemService = catItemService;
        this.serviceGBTDDefine = serviceGBTDDefine;
    }

    private final ICatItemService catItemService;
    private final ICatDepartmentService catDepartmentService;
    private final IServiceGBTDDefine serviceGBTDDefine;

    @GetMapping("/loadDefault")
    public GenericResponse<HashMap> getAllValueDefault() {
        GenericResponse<HashMap> res = new GenericResponse<>();
        HashMap<String, List> data = new HashMap<>();
        // Load ra chu ky
        data.put("timeTypes", catItemService.findByCategoryCodeAndStatus("TIME_TYPE", Const.STATUS.ACTIVE));
        // Load cac don vi voi ma user dang login duoc quyen truy cap
        data.put("departments", catDepartmentService.getDepartmentsCurrentUser(Arrays.asList(
                Const.DEPT_LEVEL.TAP_DOAN,
                Const.DEPT_LEVEL.TCT_CTY_PHB,
                Const.DEPT_LEVEL.THI_TRUONG
        )));
        res.setData(data);
        res.success();
        return res;
    }

    @PostMapping("/listDefine")
    public GenericResponse<List<ServiceGBTDDefineDto>> getAllDefineService(@RequestBody ServiceGBTDDefineDto dto) {
        GenericResponse<List<ServiceGBTDDefineDto>> res = new GenericResponse<>();
        res.setData(serviceGBTDDefine.getAllDefineService(dto.getServiceId(), dto.getDeptId()));
        res.setPaging(dto);
        res.success();
        return res;
    }

    @PostMapping("/add")
    public GenericResponse<List<ServiceGBTDDefineDto>> addDefineService(@RequestBody List<ServiceGBTDDefineDto> dtos) {
        GenericResponse<List<ServiceGBTDDefineDto>> res = new GenericResponse<>();
        serviceGBTDDefine.add(dtos);
        res.success();
        return res;
    }

    @PostMapping("/update")
    public GenericResponse<List<ServiceGBTDDefineDto>> updateDefineService(@Validated(value = Update.class)
                                                                           @RequestBody List<ServiceGBTDDefineDto> dtos) {
        GenericResponse<List<ServiceGBTDDefineDto>> res = new GenericResponse<>();
        serviceGBTDDefine.update(dtos);
        res.success();
        return res;
    }

    @PostMapping("/delete")
    public GenericResponse<Boolean> deleteById(@RequestBody ServiceGBTDDefineDto dto) {
        GenericResponse<Boolean> res = new GenericResponse<>();
        serviceGBTDDefine.delete(dto);
        res.success();
        return res;
    }

}
