package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.request.DataLookupRequest;
import com.lifesup.gbtd.dto.response.DataLookupResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.IDataLookupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data-lookup")
@Slf4j
public class DataLookupController {

    private IDataLookupService dataLookupService;

    @Autowired
    public DataLookupController(IDataLookupService dataLookupService) {
        this.dataLookupService = dataLookupService;
    }

    @GetMapping("/newest-time")
    public GenericResponse<Long> getLatestDate(Long[] serviceIds, String[] deptCodes, Long timeType) {
        return GenericResponse.success(dataLookupService.getLatestDate(serviceIds, deptCodes, timeType));
    }

    @PostMapping("/lookup")
    public GenericResponse<DataLookupResponse> doLookup(@RequestBody DataLookupRequest rq) {
        return GenericResponse.success(dataLookupService.doLookup(rq));
    }
}
