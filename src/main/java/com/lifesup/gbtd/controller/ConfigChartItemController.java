package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.ConfigChartItemDto;
import com.lifesup.gbtd.dto.object.ConfigChartRoleDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.service.inteface.IConfigChartItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config-chart-item")
@Slf4j
public class ConfigChartItemController {

    private IConfigChartItemService configChartItemService;

    @Autowired
    public ConfigChartItemController(IConfigChartItemService configChartItemService) {
        this.configChartItemService = configChartItemService;
    }
}
