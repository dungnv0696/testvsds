package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.ConfigMenuItemDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.IConfigMenuItemService;
import com.lifesup.gbtd.util.PageableCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/config-menu-item")
public class ConfigMenuItemController {
    private final IConfigMenuItemService configMenuItemService;

    @Autowired
    public ConfigMenuItemController(IConfigMenuItemService configMenuItemService) {
        this.configMenuItemService = configMenuItemService;
    }

    @GetMapping("/get-by-menu")
    public DataListResponse<ConfigMenuItemDto> getAllByMenu(ConfigMenuItemDto dto, PageableCustom pageable) {
        DataListResponse<ConfigMenuItemDto> res = new DataListResponse<>();
        Page<ConfigMenuItemDto> page = configMenuItemService.findAllByMenu(dto, pageable);
        res.setData(page.getContent());
        res.setPaging(page);
        return res;
    }

    @PostMapping("/update")
    public DataListResponse<ConfigMenuItemDto> addConfigMenuItem(@NotNull @RequestBody List<@Valid ConfigMenuItemDto> dtos) {
        DataListResponse<ConfigMenuItemDto> res = new DataListResponse<>();
        res.setData(configMenuItemService.updateConfigMenuItem(dtos));
        res.success();
        return res;
    }

    @PostMapping("/delete")
    public GenericResponse<Boolean> deleteConfigMenuItem(@RequestParam Long id) {
        GenericResponse<Boolean> res = new GenericResponse<>();
        configMenuItemService.delete(id);
        res.success();
        return res;
    }
}
