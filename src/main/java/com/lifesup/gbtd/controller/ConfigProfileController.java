package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.dto.object.ConfigProfileDto;
import com.lifesup.gbtd.dto.object.ConfigProfileRoleDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.ICatDepartmentService;
import com.lifesup.gbtd.service.inteface.ICatItemService;
import com.lifesup.gbtd.service.inteface.IConfigProfileRoleService;
import com.lifesup.gbtd.service.inteface.IConfigProfileService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.PageableCustom;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/config-profiles")
@Slf4j
public class ConfigProfileController {

    private final IConfigProfileService configProfileService;
    private final ICatDepartmentService catDepartmentService;
    private final IConfigProfileRoleService configProfileRoleService;
    private final ICatItemService catItemService;

    @Autowired
    public ConfigProfileController(IConfigProfileService configProfileService,
                                   ICatDepartmentService catDepartmentService,
                                   IConfigProfileRoleService configProfileRoleService,
                                   ICatItemService catItemService) {
        this.configProfileService = configProfileService;
        this.catDepartmentService = catDepartmentService;
        this.configProfileRoleService = configProfileRoleService;
        this.catItemService = catItemService;
    }

    @PostMapping("/add")
    public GenericResponse<ConfigProfileDto> createConfigProfile(@Validated(value = Add.class) @RequestBody ConfigProfileDto configProfileDTO) {
        log.debug("REST request to save ConfigProfile : {}", configProfileDTO);
        GenericResponse<ConfigProfileDto> res = new GenericResponse<>();
        configProfileService.save(configProfileDTO);
        res.success();
        return res;
    }

    @PostMapping("/update")
    public GenericResponse<ConfigProfileDto> updateConfigProfile(@Validated(value = Update.class) @RequestBody ConfigProfileDto configProfileDTO) {
        GenericResponse<ConfigProfileDto> res = new GenericResponse<>();
        configProfileService.update(configProfileDTO);
        res.success();
        return res;
    }

    @GetMapping("/delete")
    public GenericResponse<Boolean> deleteConfigProfile(@RequestParam Long id) {
        GenericResponse<Boolean> res = new GenericResponse<>();
        configProfileService.delete(id);
        res.success();
        return res;
    }

    @GetMapping("/get")
    public DataListResponse<ConfigProfileDto> searchConfigProfiles(ConfigProfileDto configProfileDto, PageableCustom pageable) {
        DataListResponse<ConfigProfileDto> res = new DataListResponse<>();
        Page<ConfigProfileDto> page = configProfileService.getAllConfigProfiles(configProfileDto, pageable);
        res.setData(page.getContent());
        res.setPaging(page);
        return res;
    }

    @GetMapping("/get/{id}")
    public GenericResponse<ConfigProfileDto> getConfigProfile(@PathVariable Long id) {
        GenericResponse<ConfigProfileDto> res = new GenericResponse<>();
        res.setData(configProfileService.findById(id));
        res.success();
        return res;
    }

    // --------------- Gan Quyen Profile ----------------
    @PostMapping("/permission/update")
    public GenericResponse addPermissionProfile(@NotNull @RequestBody List<@Valid ConfigProfileRoleDto> dtos) {
        GenericResponse res = new GenericResponse<>();
        configProfileRoleService.updateConfigProfileRole(dtos);
        res.success();
        return res;
    }

    @GetMapping("/permission/delete")
    public GenericResponse deleteConfigProfileRole(@RequestParam Long id) {
        GenericResponse res = new GenericResponse<>();
        configProfileRoleService.deleteConfigProfileRole(id);
        res.success();
        return res;
    }

    @GetMapping("/permission/find")
    public DataListResponse findProfileRoles(@RequestParam Long profileId, PageableCustom pageable) {
        DataListResponse res = new DataListResponse<>();
        Page<ConfigProfileRoleDto> page = configProfileRoleService.findProfileRoles(profileId, pageable);
        res.setData(page.getContent());
        res.setPaging(page);
        return res;
    }

    // Lay ra cac loai quyen
    @GetMapping("/permission")
    public DataListResponse<CatItemDto> findProfileRoles() {
        DataListResponse res = new DataListResponse<>();
        res.setData(catItemService.findByCategoryCodeAndStatus("ROLE_CODE", Const.STATUS.ACTIVE));
        return res;
    }

    // --------------- Copy Profile ----------------

    @PostMapping("/copy/{id}")
    public GenericResponse copyConfigProfile(@PathVariable Long id) {
        GenericResponse res = new GenericResponse<>();
        configProfileService.copy(id);
        res.success();
        return res;
    }

    @GetMapping("/get-for-combo")
    public DataListResponse<ConfigProfileDto> getForCombo(ConfigProfileDto configProfileDto, PageableCustom pageable) {
        DataListResponse<ConfigProfileDto> res = new DataListResponse<>();
        res.setData(configProfileService.getAllConfigProfilesAndOrderBy(configProfileDto, pageable));
        return res;
    }
}

