package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.CatLocationDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.service.inteface.ICatLocationService;
import com.lifesup.gbtd.util.PageableCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cat-location")
@Slf4j
public class CatLocationController {

    private final ICatLocationService iCatLocationService;

    @Autowired
    public CatLocationController(ICatLocationService iCatLocationService) {
        this.iCatLocationService = iCatLocationService;
    }

    @GetMapping("/get-for-combo")
    public DataListResponse<CatLocationDto> getForCombo(CatLocationDto catLocationDto, PageableCustom pageable) {
        return DataListResponse.success(iCatLocationService.getLocationForDeptLevel(catLocationDto, pageable));
    }
}
