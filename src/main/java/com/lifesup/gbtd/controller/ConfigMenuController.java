package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.object.ConfigDashboardDto;
import com.lifesup.gbtd.dto.object.ConfigMenuDto;
import com.lifesup.gbtd.dto.object.ConfigMenuItemDto;
import com.lifesup.gbtd.dto.response.DataListResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.service.inteface.IConfigDashboardService;
import com.lifesup.gbtd.service.inteface.IConfigMenuItemService;
import com.lifesup.gbtd.service.inteface.IConfigMenuService;
import com.lifesup.gbtd.service.inteface.IConfigProfileService;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.PageableCustom;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ConfigMenuController {

    private final Logger log = LoggerFactory.getLogger(ConfigMenuController.class);

    private final IConfigMenuService configMenuService;
    private final IConfigMenuItemService configMenuItemService;
    private final IConfigDashboardService configDashboardService;
    private final IConfigProfileService configProfileService;

    @Autowired
    public ConfigMenuController(IConfigMenuService configMenuService, IConfigMenuItemService configMenuItemService,
                                IConfigDashboardService configDashboardService, IConfigProfileService configProfileService) {
        this.configMenuService = configMenuService;
        this.configMenuItemService = configMenuItemService;
        this.configDashboardService = configDashboardService;
        this.configProfileService = configProfileService;
    }

    /**
     * {@code GET  /config-menus} : get all the configMenus by status = 1
     * @return the DataListResponse with status {@code 200 (OK)} and the list of configMenus in body.
     */
    @GetMapping("/config-menus")
    public DataListResponse<ConfigMenuDto> getAllConfigMenus(ConfigMenuDto dto) {
        DataListResponse<ConfigMenuDto> res = new DataListResponse<>();
        res.setData(configMenuService.getAllConfigMenus(dto));
        res.success();
        return res;
    }

    @GetMapping("/config-menus/doSearch")
    public DataListResponse<ConfigMenuDto> doSearch(ConfigMenuDto dto, PageableCustom pageable) {
        DataListResponse<ConfigMenuDto> res = new DataListResponse<>();
        Page<ConfigMenuDto> page = configMenuService.doSearch(dto, pageable);
        res.setData(page.getContent());
        res.setPaging(page);
        return res;
    }

    @PostMapping("/config-menus/add")
    public GenericResponse<ConfigMenuDto> addConfigMenu(@Validated(value = Add.class)
                                                          @RequestBody ConfigMenuDto configMenuDto) {
        return GenericResponse.success(configMenuService.create(configMenuDto));
    }

    @PostMapping("/config-menus/update")
    public GenericResponse<ConfigMenuDto> updateConfigMenu(@Validated(value = Update.class)
                                                        @RequestBody ConfigMenuDto configMenuDto) {
        return GenericResponse.success(configMenuService.update(configMenuDto));
    }

    @GetMapping("/config-menus/check-delete")
    public GenericResponse<Boolean> checkDelete(@RequestParam Long id) {
        configMenuService.checkDelete(id);
        return GenericResponse.success(true);
    }

    @PostMapping("/config-menus/delete")
    public GenericResponse<Boolean> deleteConfigMenu(@RequestParam Long id) {
        GenericResponse<Boolean> res = new GenericResponse<>();
        configMenuService.delete(id);
        res.success();
        return res;
    }

    @GetMapping("/config-menus/{id}")
    public GenericResponse<ConfigMenuDto> getConfigMenu(@PathVariable Long id, Long[] profileIds, Boolean showAllItems) {

        log.debug("REST request to get ConfigMenu : {}", id);
        ConfigMenuDto configMenuDto = configMenuService.findOne(id);
        //Get menuItem by config menu (1 -> n)
        List<ConfigMenuItemDto> menuItems = configMenuItemService.findAll(null, new Long[]{id}, null, null);
        Long[] menuItemIds = menuItems.stream().map(ConfigMenuItemDto::getId).toArray(Long[]::new);

        // Get list config dashboard
        List<ConfigDashboardDto> dashboardDtos = configDashboardService.findAll(null, profileIds, null, menuItemIds, null, null);

        menuItems.forEach(item -> {
            List<Long> dashboardIds = dashboardDtos.stream()
                    .filter(s -> item.getId().equals(s.getMenuItemId()))
                    .sorted((a, b) -> {
                        if (a.getOrderIndex() == null && b.getOrderIndex() != null) return 1;
                        if (a.getOrderIndex() == null && b.getOrderIndex() == null) return 1;
                        if (a.getOrderIndex() != null && b.getOrderIndex() == null) return -1;
                        return a.getOrderIndex().compareTo(b.getOrderIndex());
                    })
                    .map(ConfigDashboardDto::getId)
                    .collect(Collectors.toList());
            item.setDashboardIds(dashboardIds);
        });

        if (showAllItems == null || !showAllItems) {
            menuItems = menuItems.stream().filter(i -> !DataUtil.isNullOrEmpty(i.getDashboardIds())).collect(Collectors.toList());
        }
        configMenuDto.setItems(menuItems);

        GenericResponse<ConfigMenuDto> res = new GenericResponse<>();
        res.setData(configMenuDto);
        res.success();
        return res;
    }

    @GetMapping("/config-menus/get-by-profile")
    public DataListResponse<ConfigMenuDto> getAllByProfileId(Long profileId) {
        log.debug("REST request to get a page of ConfigMenus");
        configProfileService.findById(profileId);
        DataListResponse<ConfigMenuDto> res = new DataListResponse<>();
        res.setData(configMenuService.findAllByProfileIds(new Long[]{profileId}));
        res.success();
        return res;
    }
}
