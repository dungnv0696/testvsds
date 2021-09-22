package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.dto.object.ConfigAreaDto;
import com.lifesup.gbtd.dto.object.ConfigDashboardDto;
import com.lifesup.gbtd.dto.object.ConfigMapChartAreaDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.IConfigDashboardService;
import com.lifesup.gbtd.util.PageableCustom;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/config-dashboard")
@Slf4j
public class ConfigDashboardController {

    private IConfigDashboardService configDashboardService;

    @Autowired
    public ConfigDashboardController(IConfigDashboardService configDashboardService) {
        this.configDashboardService = configDashboardService;
    }

    @GetMapping
    public DataListResponse<ConfigDashboardDto> doSearch(ConfigDashboardDto dto, PageableCustom pageable) {
        return DataListResponse.success(configDashboardService.doSearch(dto, pageable));
    }

    @PostMapping
    public GenericResponse<ConfigDashboardDto> create(@Validated(value = Add.class) @RequestBody ConfigDashboardDto dto) {
        return GenericResponse.success(configDashboardService.create(dto));
    }

    @PostMapping("/update")
    public GenericResponse<ConfigDashboardDto> update(@Validated(value = Update.class) @RequestBody ConfigDashboardDto dto) {
        return GenericResponse.success(configDashboardService.update(dto));
    }

    @PostMapping("/delete")
    public GenericResponse<Boolean> delete(@RequestParam Long id) {
        GenericResponse<Boolean> res = new GenericResponse<>();
        configDashboardService.delete(id);
        res.success();
        return res;
    }

    @GetMapping("/{id}")
    public GenericResponse<ConfigDashboardDto> findById(@PathVariable Long id, ConfigDashboardDto filter) {
        return GenericResponse.success(configDashboardService.findDashboardWithFilter(id, filter));
    }

    @GetMapping("/test")
    public GenericResponse<ConfigDashboardDto> testReponse(@RequestBody ConfigDashboardDto dto) {
        ConfigDashboardDto configDashboardDto;
        if (StringUtils.isEmpty(dto.getKeyword())) {
            configDashboardDto = new ConfigDashboardDto();
            List<ConfigAreaDto> list = new ArrayList<>();
            ConfigAreaDto areaDto = new ConfigAreaDto();
            areaDto.setAreaName("test");
            areaDto.setMapCharts(Collections.singletonList(new ConfigMapChartAreaDto()));
            list.add(areaDto);
            configDashboardDto.setConfigAreaDtos(list);
        } else {
            configDashboardDto = dto;
        }
        return GenericResponse.success(configDashboardDto);
    }

    @GetMapping("/copy/{id}")
    public GenericResponse<ConfigDashboardDto> copy(@PathVariable Long id) {
        return GenericResponse.success(configDashboardService.copy(id));
    }

    @GetMapping("/get-time-type-by-service-id")
    public DataListResponse<CatItemDto> getTimeTypeByServiceId(Long[] serviceIds) {
        return DataListResponse.success(configDashboardService.getTimeTypeByServiceId(serviceIds));
    }
}
