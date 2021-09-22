package com.lifesup.gbtd.controller;


import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
import com.lifesup.gbtd.dto.object.TempReportDto;
import com.lifesup.gbtd.dto.object.UsersDto;
import com.lifesup.gbtd.dto.object.ReportDto;
import com.lifesup.gbtd.dto.request.ServiceTargetResDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.dto.response.ResponseCommon;
import com.lifesup.gbtd.service.inteface.IDynamicReportService;
import com.lifesup.gbtd.service.inteface.ITestReportService;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Delete;
import com.lifesup.gbtd.validator.group.Find;
import com.lifesup.gbtd.validator.group.Share;
import com.lifesup.gbtd.validator.group.Update;
import lombok.extern.slf4j.Slf4j;
import com.lifesup.gbtd.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/dynamicReport")
@Slf4j
public class DynamicReportController {

    private IDynamicReportService iDynamicReportService;
    private ITestReportService reportService;

    @Autowired
    public DynamicReportController(IDynamicReportService iDynamicReportService,ITestReportService reportService) {
        this.iDynamicReportService = iDynamicReportService;
        this.reportService = reportService;
    }

    @GetMapping("/loadDefault")
    public GenericResponse<HashMap> getCatDepartmentAndCatItem() {
        GenericResponse<HashMap> res = new GenericResponse<>();
        res.setData(iDynamicReportService.getCatItems());
        res.success();
        return res;
    }

    @GetMapping("/bookmarks")
    public GenericResponse<List<TempReportDto>> getListTempReport() {
        GenericResponse<List<TempReportDto>> res = new GenericResponse<>();
        res.setData(iDynamicReportService.getListTempReport());
        res.success();
        return res;
    }

    @PostMapping("/findTempReportById")
    public GenericResponse<TempReportDto> getTempReport(@Validated(Find.class) @RequestBody TempReportDto dto) {
        GenericResponse<TempReportDto> res = new GenericResponse<>();
        res.setData(iDynamicReportService.getTempReport(dto));
        res.success();
        return res;
    }

    @PostMapping("/add")
    public GenericResponse add(@Validated(Add.class) @RequestBody TempReportDto dto) {
        GenericResponse res = new GenericResponse<>();
        iDynamicReportService.add(dto);
        res.success();
        return res;
    }

    @PostMapping("/update")
    public GenericResponse update(@Validated(Update.class) @RequestBody TempReportDto dto) {
        GenericResponse res = new GenericResponse<>();
        iDynamicReportService.update(dto);
        res.success();
        return res;
    }

    @PostMapping("/delete")
    public GenericResponse delete(@Validated(Delete.class) @RequestBody TempReportDto dto) {
        GenericResponse res = new GenericResponse<>();
        iDynamicReportService.delete(dto);
        res.success();
        return res;
    }

    @PostMapping("/loadTargets")
    public GenericResponse<List<ServiceGBTDDto>> getTargetService(@Validated(Find.class) @RequestBody ServiceGBTDDto resDto) {
        GenericResponse<List<ServiceGBTDDto>> res = new GenericResponse<>();
        res.setData(iDynamicReportService.getListTargetService(resDto));
        res.success();
        return res;
    }

    @GetMapping("/loadEmployees")
    public GenericResponse<List<UsersDto>> getShareReport() {
        GenericResponse<List<UsersDto>> res = new GenericResponse<>();
        res.setData(iDynamicReportService.getListUsers());
        res.success();
        return res;
    }

    @PostMapping("/shareReport")
    public GenericResponse shareReport(@Validated(Share.class) @RequestBody TempReportDto resDto) {
        GenericResponse res = new GenericResponse<>();
        iDynamicReportService.shareReport(resDto);
        res.success();
        return res;
    }

    //    @PostMapping("/deptTree")
//    public DataListResponse<CatDepartmentDto> deptTree(@RequestBody CatDepartmentDto criteria) {
//        DataListResponse<CatDepartmentDto> res = new DataListResponse<>();
//        res.setData(iDynamicReportService.getDepartmentTreeFromDeptId(criteria));
//        res.success();
//        return res;
//    }
    @PostMapping("/deptTree")
    public ResponseCommon getUnit(@RequestBody ReportDto reportDTO) {
        ResponseCommon res = new ResponseCommon();
        res.setContent(reportService.getUnitDeptTree(reportDTO));
        res.setErrorCode(Const.ERROR_CODE.SUCCESS);
        res.setErrorMessage(Const.SUCCESS);
        return res;
    }
}
