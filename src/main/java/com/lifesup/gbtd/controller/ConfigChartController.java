package com.lifesup.gbtd.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lifesup.gbtd.dto.object.ChartParamDto;
import com.lifesup.gbtd.dto.object.ChartResultDto;
import com.lifesup.gbtd.dto.object.ConfigChartDto;
import com.lifesup.gbtd.dto.object.ConfigChartRoleDto;
import com.lifesup.gbtd.dto.object.SaveChartDto;
import com.lifesup.gbtd.dto.object.TableDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.IConfigChartRoleService;
import com.lifesup.gbtd.service.inteface.IConfigChartService;
import com.lifesup.gbtd.util.PageableCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/config-chart")
@Slf4j
public class ConfigChartController {

    private final IConfigChartService configChartService;
    private final IConfigChartRoleService configChartRoleService;

    @Autowired
    public ConfigChartController(IConfigChartService configChartService, IConfigChartRoleService configChartRoleService) {
        this.configChartService = configChartService;
        this.configChartRoleService = configChartRoleService;
    }

    @GetMapping("/get-chart-result/{id}")
    public ResponseEntity<ChartResultDto> getData(@PathVariable Long id, ChartParamDto params) throws JsonProcessingException, ParseException {
        return new ResponseEntity<>(configChartService.buildChart(id, params), null, HttpStatus.OK);
    }
//
//    @GetMapping("/get-all")
//    public DataListResponse<ConfigChartDto> findUserChartByType(ConfigChartDto dto) {
//        return DataListResponse.success(configChartService.findUserChartByType(dto));
//    }

    @GetMapping("/get-all")
    public DataListResponse<ConfigChartDto> doSearch(ConfigChartDto dto, PageableCustom pageable) {
        return DataListResponse.success(configChartService.doSearch(dto, pageable));
    }

    @GetMapping("/get-table-description")
    public DataListResponse<TableDto> getDescriptionTableToMap(@RequestParam String tableName) {
        return DataListResponse.success(configChartService.getDescriptionOfTableToMap(tableName));
    }

    @PostMapping("/create")
    public GenericResponse<ChartResultDto> createChart(@RequestBody SaveChartDto configChartDTO) {
        return GenericResponse.success(configChartService.createConfigChart(configChartDTO));
    }

    @GetMapping("/{id}")
    public GenericResponse<SaveChartDto> createChart(@PathVariable Long id) {
        return GenericResponse.success(configChartService.getConfigChart(id));
    }

    @PostMapping("/update")
    public GenericResponse<ChartResultDto> updateChart(@RequestBody SaveChartDto configChartDTO) {
        return GenericResponse.success(configChartService.updateConfigChart(configChartDTO));
    }

    @PostMapping("/preview")
    public GenericResponse<ChartResultDto> preview(@RequestBody SaveChartDto configChartDTO) throws JsonProcessingException, ParseException {
        return GenericResponse.success(configChartService.previewChart(configChartDTO));
    }

    @GetMapping("/copy/{id}")
    public GenericResponse<ConfigChartDto> copy(@PathVariable Long id) {
        return GenericResponse.success(configChartService.copy(id));
    }

    @GetMapping("/delete")
    public GenericResponse<Boolean> delete(@RequestParam Long id) {
        GenericResponse<Boolean> res = new GenericResponse<>();
        configChartService.delete(id);
        res.success();
        return res;
    }

    @GetMapping("/check-delete")
    public GenericResponse<Boolean> checkDelete(@RequestParam Long id) {
        configChartService.checkDelete(id);
        return GenericResponse.success(true);
    }

    // --------------- Gan Quyen Chart ----------------
    @PostMapping("/permission/update")
    public GenericResponse addPermissionChart(@NotNull @RequestBody List<@Valid ConfigChartRoleDto> dtos) {
        GenericResponse res = new GenericResponse<>();
        configChartRoleService.updateConfigChartRole(dtos);
        res.success();
        return res;
    }

    @GetMapping("/permission/delete")
    public GenericResponse<Boolean> deleteConfigChartRole(@RequestParam Long id) {
        GenericResponse<Boolean> res = new GenericResponse<>();
        configChartRoleService.deleteConfigChartRole(id);
        res.success();
        return res;
    }
}
