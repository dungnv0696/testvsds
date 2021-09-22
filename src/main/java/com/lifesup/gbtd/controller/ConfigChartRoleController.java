package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.ConfigChartRoleDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.service.inteface.IConfigChartRoleService;
import com.lifesup.gbtd.util.PageableCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config-chart-role")
@Slf4j
public class ConfigChartRoleController {

    private IConfigChartRoleService configChartRoleService;

    @Autowired
    public ConfigChartRoleController(IConfigChartRoleService configChartRoleService) {
        this.configChartRoleService = configChartRoleService;
    }

    @GetMapping("/get")
    public DataListResponse<ConfigChartRoleDto> get(ConfigChartRoleDto dto, PageableCustom pageable) {
        DataListResponse<ConfigChartRoleDto> res = new DataListResponse<>();
        res.setData(configChartRoleService.get(dto, pageable));
        res.setPaging(dto);
        res.success();
        return res;
    }
}
