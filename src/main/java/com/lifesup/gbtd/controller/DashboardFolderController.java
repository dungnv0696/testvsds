package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.DashboardFolderDto;
import com.lifesup.gbtd.dto.object.TreeDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.IDashboardFolderService;
import com.lifesup.gbtd.util.PageableCustom;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/dashboard-folder")
@Slf4j
public class DashboardFolderController {
    private final IDashboardFolderService iDashboardFolderService;

    @Autowired
    public DashboardFolderController(IDashboardFolderService iDashboardFolderService) {
        this.iDashboardFolderService = iDashboardFolderService;
    }

    @GetMapping("/get-for-combo")
    public DataListResponse<DashboardFolderDto> getForCombo() {
        return DataListResponse.success(iDashboardFolderService.getDashboardFolderByFolderId(new ArrayList<>()));
    }

    @GetMapping("/get-for-combo-tree")
    public DataListResponse<DashboardFolderDto> getForComboTree(DashboardFolderDto dto) {
        return DataListResponse.success(iDashboardFolderService.getDashboardFolderTree(dto));
    }

    @GetMapping("/get")
    public DataListResponse<DashboardFolderDto> doSearch(DashboardFolderDto dto, PageableCustom pageable) {
        return DataListResponse.success(iDashboardFolderService.doSearch(dto, pageable));
    }

    @GetMapping("/{id}")
    public GenericResponse<DashboardFolderDto> findById(@PathVariable Long id) {
        return GenericResponse.success(iDashboardFolderService.findById(id));
    }

    @PostMapping("/update")
    public GenericResponse<DashboardFolderDto> update(@Validated(value = Update.class) @RequestBody DashboardFolderDto dto) {
        return GenericResponse.success(iDashboardFolderService.update(dto));
    }

    @PostMapping("/create")
    public GenericResponse<DashboardFolderDto> create(@Validated(value = Add.class) @RequestBody DashboardFolderDto dto) {
        return GenericResponse.success(iDashboardFolderService.create(dto));
    }

    @PostMapping("/lock")
    public GenericResponse<DashboardFolderDto> changeFolderStatus(@RequestBody DashboardFolderDto dto) {
        return GenericResponse.success(iDashboardFolderService.changeFolderStatus(dto));
    }

    @GetMapping("/get-folder-tree")
    public DataListResponse<TreeDto> getFolderTree(){
        return DataListResponse.success(iDashboardFolderService.getFolderAndFile());
    }
}
