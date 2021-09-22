package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.CatGroupChartDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.service.inteface.ICatGroupChartService;
import com.lifesup.gbtd.util.PageableCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cat-group-chart")
@Slf4j
public class CatGroupChartController {

    private ICatGroupChartService iCatGroupChartService;

    @Autowired
    public CatGroupChartController(ICatGroupChartService iCatGroupChartService) {
        this.iCatGroupChartService = iCatGroupChartService;
    }

    @GetMapping
    public DataListResponse<CatGroupChartDto> getAllCatGroupCharts(Long dashboardId, Long status, PageableCustom pageable) {
        DataListResponse<CatGroupChartDto> res = new DataListResponse<>();
        Page<CatGroupChartDto> catGroupChartDtos = iCatGroupChartService.findAllCatGroups(dashboardId, status, pageable);
        res.setData(catGroupChartDtos.getContent());
        res.setPaging(catGroupChartDtos);
        return res;
    }

    @GetMapping("/get-all")
    public DataListResponse<CatGroupChartDto> getAllCatGroupCharts(CatGroupChartDto dto, PageableCustom pageable) {
        DataListResponse<CatGroupChartDto> res = new DataListResponse<>();
        Page<CatGroupChartDto> catGroupChartDtos = iCatGroupChartService.findAllCatGroups(dto, pageable);
        res.setData(catGroupChartDtos.getContent());
        res.setPaging(catGroupChartDtos);
        return res;
    }
}
