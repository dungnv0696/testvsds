//package com.lifesup.gbtd.controller;
//
//import com.lifesup.gbtd.dto.response.GenericResponse;
//import com.lifesup.gbtd.service.CatDepartmentServiceImpl;
//import com.lifesup.gbtd.service.CatItemServiceImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/target")
//@Slf4j
//public class TargetCategoryController {
//
//    @Autowired
//    public TargetCategoryController(CatItemServiceImpl catItemService, CatDepartmentServiceImpl catDepartmentService) {
//        this.catItemService = catItemService;
//        this.catDepartmentService = catDepartmentService;
//    }
//
//
//
//    @GetMapping("/loadDefault")
//    public GenericResponse<HashMap> getAllValueDefault() {
//        GenericResponse<HashMap> res = new GenericResponse<>();
//        HashMap<String, List> data = new HashMap<>();
//
//        data.put("targetType", catItemService.findByCategoryCode("SERVICE_TYPE"));
//        data.put("targetGroup", catItemService.findByCategoryCode("GROUP_KPI"));
//        // TO DO current user
//        data.put("departments", catDepartmentService.getDepartments("currentuser"));
//        res.setData(data);
//        res.success();
//        return res;
//    }
//}
