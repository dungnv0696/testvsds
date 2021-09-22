package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.ReportDto;
import com.lifesup.gbtd.dto.object.TempReportDto;
import com.lifesup.gbtd.dto.response.ResponseCommon;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.service.inteface.IDynamicReportService;
import com.lifesup.gbtd.service.inteface.ITestReportService;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/testreport")
@Slf4j
public class TestReportController {

    private ITestReportService reportService;
    private IDynamicReportService dynamicReportService;

    @Autowired
    public TestReportController(ITestReportService reportService, IDynamicReportService dynamicReportService) {
        this.reportService = reportService;
        this.dynamicReportService = dynamicReportService;
    }

    @PostMapping("/getAll")
    public ResponseCommon getAll(@RequestBody ReportDto dataSearch) {
        ResponseCommon res;
        res = reportService.generateReport(dataSearch);
        return res;
    }

    @PostMapping("/getUnit")
    public ResponseCommon getUnit(@RequestBody ReportDto reportDTO) {
        ResponseCommon res = new ResponseCommon();
        res.setContent(reportService.getUnit(reportDTO));
        res.setErrorCode(Const.ERROR_CODE.SUCCESS);
        res.setErrorMessage(Const.SUCCESS);
        return res;
    }

    @GetMapping("/getTempReport")
    public ResponseCommon getTempReport() {
        // todo get userid
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String userId = ((UserToken) request.getSession().getAttribute("vsaUserToken")).getUserName();
        ResponseCommon res = new ResponseCommon();
        try {
            res.setContent(dynamicReportService.getListTempReport());
            res.setErrorCode(Const.ERROR_CODE.SUCCESS);
            res.setErrorMessage(Const.SUCCESS);
        } catch (Exception e) {
            log.error("error", e);
            res.setErrorCode(Const.ERROR_CODE.FAIL);
            res.setErrorMessage(Const.ERROR);
        }
        return res;
    }

    @PostMapping("/insertTempReport")
    public ResponseCommon insertTempReport(@RequestBody TempReportDto dto) {
        ResponseCommon res = new ResponseCommon();
//        reportService.insertTempReport(tempReportDTO, userName);
//        reportService.insertTempReport(dto, "u1");
        try {
            res.setErrorCode(Const.ERROR_CODE.SUCCESS);
            res.setErrorMessage(Const.SUCCESS);
            res.setContent(dynamicReportService.add(dto));
        } catch (Exception e) {
            log.error("add error", e);
            this.handleExceptionOldResponse(res, e);
        }
        return res;
    }

    @PostMapping("/updateTempReport")
    public ResponseCommon updateTempReport(@RequestBody TempReportDto tempReportDTO) {
        ResponseCommon res = new ResponseCommon();
        try {
            dynamicReportService.update(tempReportDTO);
            res.setErrorCode(Const.ERROR_CODE.SUCCESS);
            res.setErrorMessage(Const.SUCCESS);
        } catch (Exception e) {
            log.error("Update error", e);
            this.handleExceptionOldResponse(res, e);
        }
        return res;
    }

    @PostMapping("/deleteTempReport")
    public ResponseCommon deleteTempReport(@RequestBody TempReportDto tempReportDTO) {
        ResponseCommon res = new ResponseCommon();
        reportService.deleteTempReport(tempReportDTO);
        res.setErrorCode(Const.ERROR_CODE.SUCCESS);
        res.setErrorMessage(Const.SUCCESS);
        return res;
    }

    @GetMapping("/getSheet")
    public ResponseCommon getSheet() {
        ResponseCommon rs = new ResponseCommon();
        rs.setErrorCode(Const.ERROR_CODE.SUCCESS);
        rs.setContent(reportService.getSheet());
        return rs;
    }

    @GetMapping("/download")
    public ResponseEntity downloadReport(@RequestParam String fileName) {
        ResponseEntity<Resource> res = reportService.downloadReport(fileName);
        if (res == null) {
            ResponseCommon rs = new ResponseCommon();
            rs.setErrorMessage("NOT FOUND");
            return ResponseEntity.ok(rs);
        } else {
            return res;
        }
    }

    private void handleExceptionOldResponse(ResponseCommon res, Exception e) {
        if (e instanceof ServerException) {
            res.setErrorCode(Const.ERROR_CODE.FAIL);
            res.setErrorMessage(((ServerException) e).getErrorCode().getMessage());
        } else {
            res.setErrorCode(Const.ERROR_CODE.FAIL);
            res.setErrorMessage(Const.ERROR);
        }
    }
}
