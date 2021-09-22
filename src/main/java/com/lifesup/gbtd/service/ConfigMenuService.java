package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.ConfigDashboardEntity;
import com.lifesup.gbtd.model.ConfigMenuEntity;
import com.lifesup.gbtd.model.ConfigMenuItemEntity;
import com.lifesup.gbtd.repository.ConfigDashboardRepository;
import com.lifesup.gbtd.repository.ConfigMenuItemRepository;
import com.lifesup.gbtd.repository.ConfigMenuRepository;
import com.lifesup.gbtd.repository.ConfigProfileRepository;
import com.lifesup.gbtd.service.inteface.IConfigMenuService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.MessageUtil;
import org.eclipse.birt.report.data.adapter.i18n.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class ConfigMenuService extends BaseService implements IConfigMenuService {

    private final ConfigMenuRepository configMenuRepository;
    private final ConfigProfileRepository configProfileRepository;
    private final ConfigMenuItemRepository configMenuItemRepository;
    private final ConfigDashboardRepository configDashboardRepository;
    private final UserLogService userLogService;

    @Autowired
    public ConfigMenuService(ConfigMenuRepository configMenuRepository,
                             ConfigProfileRepository configProfileRepository,
                             ConfigMenuItemRepository configMenuItemRepository,
                             ConfigDashboardRepository configDashboardRepository, UserLogService userLogService) {
        this.configMenuRepository = configMenuRepository;
        this.configProfileRepository = configProfileRepository;
        this.configMenuItemRepository = configMenuItemRepository;
        this.configDashboardRepository = configDashboardRepository;
        this.userLogService = userLogService;
    }

    @Override
    public List<ConfigMenuDto> getAllConfigMenus(ConfigMenuDto dto) {
        return super.mapList(configMenuRepository.getAllConfigMenu(dto), ConfigMenuDto.class);
//        return super.mapList(configMenuRepository.findByStatusOrderByOrderIndexAscMenuNameAsc(Const.STATUS.ACTIVE), ConfigMenuDto.class);
    }

    @Override
    public List<ConfigMenuItemDto> getConfigMenuItemsByMenuIdAndProfileId(Long menuId, Long profileId) {
        if (Objects.isNull(menuId) || Objects.isNull(profileId)) {
            throw new ServerException(ErrorCode.MISSING_PARAMS);
        }
        return configMenuRepository.getConfigMenuItemsByMenuIdAndProfileId(menuId, profileId);
    }

    @Override
    public ConfigMenuDto findOne(Long id) {
        Optional<ConfigMenuEntity> entity = configMenuRepository.findById(id);
        if (!entity.isPresent()) {
            throw new ServerException(ErrorCode.NOT_FOUND);
        }
        return super.map(entity.get(), ConfigMenuDto.class);
    }

    @Override
    public List<ConfigMenuDto> findAllByProfileIds(Long[] profileIds) {

        List list = configMenuRepository.findAllByProfileIds(profileIds);

        List<ConfigMenuDto> rs = new ArrayList<>();
        for (Object o : list) {
            Object[] arrObj = (Object[]) o;
            ConfigMenuEntity menu = (ConfigMenuEntity) arrObj[0];
            ConfigMenuItemEntity menuItem = (ConfigMenuItemEntity) arrObj[1];
            ConfigDashboardEntity dashboard = (ConfigDashboardEntity) arrObj[2];
            if (menu != null) {
                ConfigMenuDto dto = super.map(menu, ConfigMenuDto.class);
                boolean isExisted = false;
                Optional<ConfigMenuDto> ckDto = rs.stream().filter(i -> menu.getId().equals(i.getId())).findFirst();
                if (ckDto.isPresent()) {
                    dto = ckDto.get();
                    isExisted = true;
                }
                boolean isItemExisted = false;
                if (menuItem != null && dashboard != null) {
                    ConfigMenuItemDto menuItemDto = super.map(menu, ConfigMenuItemDto.class);
                    Optional<ConfigMenuItemDto> ckMenuItemDto = dto.getItems().stream().filter(i -> menuItem.getId().equals(i.getId())).findFirst();
                    if (ckMenuItemDto.isPresent()) {
                        menuItemDto = ckMenuItemDto.get();
                        isItemExisted = true;
                    }
                    List<Long> dashboardIds = menuItemDto.getDashboardIds();
                    dashboardIds.add(dashboard.getId());
                    menuItemDto.setDashboardIds(dashboardIds);
                    if (!isItemExisted) {
                        List<ConfigMenuItemDto> items = dto.getItems();
                        items.add(menuItemDto);
                        dto.setItems(items);
                    }
                }
                if (!isExisted) {
                    rs.add(dto);
                }
            }
        }

        return rs;
    }

    @Override
    public Page<ConfigMenuDto> doSearch(ConfigMenuDto dto, Pageable pageable) {
        List<Long> profileIds = new ArrayList<>();
        Map<Long, ConfigProfileDto> map = new HashMap<>();
        List<ConfigProfileDto> configProfileDtos = configProfileRepository
                .getAllConfigProfilesAndOrderBy(new ConfigProfileDto(), super.getCurrentUsername(), super.getCurrentUserDeptId(), null);

        if (Objects.nonNull(dto.getProfileId())) {
            configProfileDtos.forEach(e -> {
                map.put(e.getId(), e);
            });
            profileIds.add(dto.getProfileId());
        } else {
            configProfileDtos.forEach(e -> {
                map.put(e.getId(), e);
                profileIds.add(e.getId());
            });
        }
        dto.setProfileIds(profileIds);
        Page<ConfigMenuDto> dtos = configMenuRepository.getListConfigMenu(dto, pageable);
        dtos.getContent().forEach(e -> {
            e.setUserRoleCode(map.get(e.getProfileId()).getRoleCode());
        });
        //ghi log
//        UserLogDto userLogDto = new UserLogDto("GET", "SEARCH CAU_HINH_MENU", "Tìm kiếm cấu hình menu");
//        userLogService.saveLog(userLogDto);
        return dtos;
    }

    @Override
    public ConfigMenuDto create(ConfigMenuDto dto) {
        this.validateSave(dto);
        dto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.INSERT));
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "CREATE CAU_HINH_MENU", MessageUtil.getMessage("code.cau_hinh_menu.create"),objectToJson(dto));
        userLogService.saveLog(userLogDto);
        return this.save(dto);
    }

    @Override
    @Transactional
    public ConfigMenuDto update(ConfigMenuDto dto) {
        this.validateSave(dto);
        dto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.UPDATE));
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "UPDATE CAU_HINH_MENU", MessageUtil.getMessage("code.cau_hinh_menu.update"),objectToJson(dto));
        userLogService.saveLog(userLogDto);
        return this.save(dto);
    }

    @Transactional
    public ConfigMenuDto save(ConfigMenuDto dto) {
        dto.setUpdateTime(new Date());
        dto.setUpdateUser(super.getCurrentUsername());
        if (dto.getStatus() == null) {
            dto.setStatus(Const.STATUS.ACTIVE);
        }

        ActionAuditDto.Builder logMenu = dto.getLogBuilder().tableName(Const.TABLE.CONFIG_MENU);
        if (dto.getId() != null) {
            logMenu.oldValue(configMenuRepository.findById(dto.getId())
                    .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND)));
        }

        ConfigMenuEntity entity = configMenuRepository.save(super.map(dto, ConfigMenuEntity.class));
        ConfigMenuDto dtoReturn = super.map(entity, ConfigMenuDto.class);
        logMenu.objectId(entity.getId()).newValue(entity);
        super.saveLog(logMenu.build());

        return dtoReturn;
    }

    private void validateSave(ConfigMenuDto dto) {
        if (dto.getId() != null) {
            configMenuRepository.findById(dto.getId())
                    .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.CONFIG_MENU));
        }
        configProfileRepository.findById(dto.getProfileId())
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.CONFIG_PROFILE));

        configMenuRepository.findByMenuNameAndProfileId(dto.getMenuName().trim(), dto.getProfileId())
                .stream()
                .findAny()
                .ifPresent(e -> {
                    if (Objects.isNull(dto.getId()) || !dto.getId().equals(e.getId()))
                        throw new ServerException(ErrorCode.ALREADY_EXIST, "Menu name");
                });
    }

    @Override
    public void checkDelete(Long id) {
        List<ConfigMenuItemEntity> configMenuItemEntities = configMenuItemRepository.findByMenuId(id);
        configMenuItemEntities.forEach(e ->{
            List<ConfigDashboardEntity> configDashboardEntities = configDashboardRepository.findByMenuItemId(e.getId());
            if (configDashboardEntities.size() != 0) {
                throw new ServerException(ErrorCode.ACCESS_DENIED, "Can not delete Menu");
            }
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        List<ActionAuditDto> actionLogs = new ArrayList<>();
        Optional<ConfigMenuEntity> opt = configMenuRepository.findById(id);
        if (!opt.isPresent()) {
            throw new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.CONFIG_MENU);
        }
        List<ConfigMenuItemEntity> menuItemEntities = configMenuItemRepository.findByMenuId(id);
        menuItemEntities.forEach(e -> {
            actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_MENU_ITEM, e.getId(), e));
            configMenuItemRepository.delete(e);
        });
        actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_MENU, opt.get().getId(), opt.get()));
        configMenuRepository.delete(opt.get());
        super.saveLog(actionLogs);
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "DELETE CAU_HINH_MENU", MessageUtil.getMessage("code.cau_hinh_menu.delete"),objectToJson(id));
        userLogService.saveLog(userLogDto);
    }
}
