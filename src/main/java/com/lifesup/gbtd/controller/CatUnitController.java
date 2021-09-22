package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.CatUnitDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.service.inteface.ICatUnitService;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cat-unit")
@Slf4j
public class CatUnitController {

    private ICatUnitService iCatUnitService;

    public CatUnitController(ICatUnitService iCatUnitService) {
        this.iCatUnitService = iCatUnitService;
    }

    @GetMapping("/get-all")
    public DataListResponse<CatUnitDto> getAllCatUnits(CatUnitDto dto) {
        return DataListResponse.success(iCatUnitService.getAll(dto));
    }
}
