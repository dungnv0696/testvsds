package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.ConfigAreaEntity;
import com.lifesup.gbtd.model.ConfigDashboardEntity;
import com.lifesup.gbtd.model.ConfigMapChartAreaEntity;
import com.lifesup.gbtd.model.ConfigMenuEntity;
import com.lifesup.gbtd.model.ConfigMenuItemEntity;
import com.lifesup.gbtd.model.ConfigProfileEntity;
import com.lifesup.gbtd.model.ConfigProfileRoleEntity;
import com.lifesup.gbtd.repository.ConfigAreaRepository;
import com.lifesup.gbtd.repository.ConfigMenuItemRepository;
import com.lifesup.gbtd.repository.ConfigMenuRepository;
import com.lifesup.gbtd.repository.ConfigDashboardRepository;
import com.lifesup.gbtd.repository.ConfigMapChartAreaRepository;
import com.lifesup.gbtd.repository.ConfigProfileRepository;
import com.lifesup.gbtd.repository.ConfigProfileRoleRepository;
import com.lifesup.gbtd.service.inteface.IConfigProfileService;
import com.lifesup.gbtd.service.inteface.ILogActionService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing { ConfigProfile}.
 */
@Service
@Transactional
public class ConfigProfileService extends BaseService implements IConfigProfileService {

    private final Logger log = LoggerFactory.getLogger(ConfigProfileService.class);

    private final ConfigProfileRepository configProfileRepository;
    private final ConfigProfileRoleRepository configProfileRoleRepository;
    private final ILogActionService logActionService;
    private final ConfigDashboardRepository configDashBoardRepository;
    private final ConfigAreaRepository configAreaRepository;
    private final ConfigMapChartAreaRepository configMapChartAreaRepository;
    private final ConfigMenuRepository configMenuRepository;
    private final ConfigMenuItemRepository configMenuItemRepository;
    private final UserLogService userLogService;

    @Autowired
    public ConfigProfileService(ConfigProfileRepository configProfileRepository,
                                ConfigProfileRoleRepository configProfileRoleRepository,
                                ILogActionService logActionService,
                                ConfigDashboardRepository configDashBoardRepository,
                                ConfigAreaRepository configAreaRepository,
                                ConfigMapChartAreaRepository configMapChartAreaRepository,
                                ConfigMenuRepository configMenuRepository,
                                ConfigMenuItemRepository configMenuItemRepository, UserLogService userLogService) {
        this.configProfileRepository = configProfileRepository;
        this.configProfileRoleRepository = configProfileRoleRepository;
        this.logActionService = logActionService;
        this.configDashBoardRepository = configDashBoardRepository;
        this.configAreaRepository = configAreaRepository;
        this.configMapChartAreaRepository = configMapChartAreaRepository;
        this.configMenuRepository = configMenuRepository;
        this.configMenuItemRepository = configMenuItemRepository;
        this.userLogService = userLogService;
    }

    @Override
    public ConfigProfileDto save(ConfigProfileDto configProfileDto) {
        ConfigProfileDto configProfile = Optional.ofNullable(super.map(configProfileDto, ConfigProfileEntity.class))
                .map(cp -> {
                    cp.setId(null);
                    cp.setUpdateUser(ConfigProfileService.super.getCurrentUsername());
                    cp.setUpdateTime(new Date());
                    return cp;
                })
                .map(configProfileRepository::save)
                .map(cp -> ConfigProfileService.super.map(cp, ConfigProfileDto.class))
                .orElseThrow(() -> new ServerException(ErrorCode.FAILED));
        this.saveConfigProfileRole(configProfile);
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_PROFILE, Const.ACTION.INSERT,
                configProfile.getId(), null, super.map(configProfile, ConfigProfileEntity.class)));
        UserLogDto userLogDto = new UserLogDto("POST","CREATE CAU_HINH_PROFILE", MessageUtil.getMessage("code.cau_hinh_profile.create"),objectToJson(configProfileDto));
        userLogService.saveLog(userLogDto);
        return configProfile;
    }

    @Override
    public ConfigProfileDto update(ConfigProfileDto configProfileDto) {
        ConfigProfileEntity entity = configProfileRepository.findById(configProfileDto.getId())
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "ConfigProfile"));
        Long roleType = entity.getRoleType();
        String oldValue = super.toJson(entity);
        ConfigProfileDto configProfile = Optional.ofNullable(super.map(configProfileDto, ConfigProfileEntity.class))
                .map(cp -> {
                    cp.setUpdateUser(super.getCurrentUsername());
                    cp.setUpdateTime(new Date());
                    return cp;
                })
                .map(configProfileRepository::save)
                .map(b -> super.map(b, ConfigProfileDto.class))
                .orElseThrow(() -> new ServerException(ErrorCode.FAILED));
        //check role_type
        if (!configProfileDto.getRoleType().equals(roleType)) {
            configProfileRoleRepository.findByProfileId(entity.getId())
                    .stream()
                    .forEach(cpr -> deleteConfigProfileRole(cpr.getId()));
            this.saveConfigProfileRole(configProfile);
        }

        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_PROFILE, Const.ACTION.UPDATE,
                configProfile.getId(), oldValue, super.map(configProfile, ConfigProfileEntity.class)));
        UserLogDto userLogDto = new UserLogDto("POST","UPDATE CAU_HINH_PROFILE",MessageUtil.getMessage("code.cau_hinh_profile.update"),objectToJson(configProfileDto));
        userLogService.saveLog(userLogDto);
        return configProfile;
    }

    @Override
    public void delete(Long id) {
        configProfileRepository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "configProfile"));
        // delete configProfileRole
        configProfileRoleRepository.findByProfileId(id)
                .forEach(cpr -> deleteConfigProfileRole(cpr.getId()));
        // delete configMenu by profileId
        configMenuRepository.findByProfileId(id)
                .forEach(this::deleteConfigMenu);
        //delete configDashboard by profileId
        configDashBoardRepository.findByProfileId(id)
                .forEach(this::deleteConfigDashboard);

        configProfileRepository.deleteById(id);
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_PROFILE, Const.ACTION.DELETE,
                id, id, null));
        UserLogDto userLogDto = new UserLogDto("POST","DELETE CAU_HINH_PROFILE",MessageUtil.getMessage("code.cau_hinh_profile.delete"),objectToJson(id));
        userLogService.saveLog(userLogDto);
    }

    public void saveConfigProfileRole(ConfigProfileDto configProfile) {
        ConfigProfileRoleEntity cpr = new ConfigProfileRoleEntity();
        cpr.setProfileId(configProfile.getId());
        if (configProfile.getRoleType().equals(Const.ROLE_TYPE.THEO_DON_VI)) {
            cpr.setDeptId(super.getCurrentUserDeptId());
        } else if (configProfile.getRoleType().equals(Const.ROLE_TYPE.CA_NHAN)) {
            cpr.setUsernameUsed(super.getCurrentUsername());
        }
        cpr.setUpdateTime(new Date());
        cpr.setUpdateUser(super.getCurrentUsername());
        cpr.setRoleCode(Const.ROLE_CODE.ADMIN);
        ConfigProfileRoleEntity entity = configProfileRoleRepository.save(cpr);

        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_PROFILE_ROLE, Const.ACTION.INSERT,
                entity.getId(), null, entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConfigProfileDto> getAllConfigProfiles(ConfigProfileDto dto,  Pageable pageable) {
        Page<ConfigProfileDto> profileDtoPage;
        if (dto.getAction() == null || !"search".equals(dto.getAction())) {
            profileDtoPage = configProfileRepository.getAllConfigProfiles(pageable, super.getCurrentUsername(), super.getCurrentUserDeptId());
        } else {
            profileDtoPage = configProfileRepository.searchConfigProfiles(dto, pageable);
        }
        this.setUserRoleOfConfigProfile(profileDtoPage.getContent());
//        UserLogDto userLogDto = new UserLogDto("GET","SEARCH CAU_HINH_PROFILE","Tìm kiếm cấu hình profile");
//        userLogService.saveLog(userLogDto);
        return profileDtoPage;
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigProfileDto findById(Long id) {
        return configProfileRepository.findById(id)
                .map(cp -> super.map(cp, ConfigProfileDto.class))
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "ConfigProfile"));
    }

    private void setUserRoleOfConfigProfile(List<ConfigProfileDto> configProfileDtos) {
        List<Long> listId = configProfileDtos.stream()
                .map(ConfigProfileDto::getId)
                .collect(Collectors.toList());
        Map<Long, String> cprd =
            configProfileRepository.getByDeptIdAndUsernameUsedAndIds(super.getCurrentUserDeptId(), super.getCurrentUsername(), listId)
                .stream()
                .collect(Collectors.toMap(
                        ConfigProfileDto::getId,
                        ConfigProfileDto::getProfileName
                ));
        configProfileDtos.stream()
                .filter(b -> cprd.containsKey(b.getId()))
                .forEach(c -> c.setUserRoleCode(Const.ROLE_CODE.ADMIN));
    }

    @Override
    @Transactional
    public ConfigProfileDto copy(Long cloneId) {
        // Copy config_profile
        ConfigProfileDto newConfigProfileDto = this.findById(cloneId);
        newConfigProfileDto.setId(null);
        newConfigProfileDto.setIsDefault(0L);
        newConfigProfileDto.setUpdateTime(new Date());
        newConfigProfileDto.setUpdateUser(super.getCurrentUsername());

        ConfigProfileEntity cloneProfile = configProfileRepository.save(super.map(newConfigProfileDto, ConfigProfileEntity.class));
        String newProfileName = cloneProfile.getProfileName() + "_" + cloneProfile.getId().intValue();
        cloneProfile.setProfileName(newProfileName);
        configProfileRepository.save(cloneProfile);

        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_PROFILE, Const.ACTION.INSERT,
                cloneProfile.getId(), null, cloneProfile));

        // copy config_profile_role
        this.copyProfileRole(cloneId, cloneProfile.getId());

        // copy config_menu
        Map<Long, Long> mapMenuIds = new HashMap<>();
        this.copyConfigMenu(cloneId, cloneProfile.getId(), mapMenuIds);

        // copy config_menu_item
        this.copyConfigMenuItems(mapMenuIds);

        // copy config_dashboard
        Map<Long, Long> mapDashboardIds = new HashMap<>();
        this.copyDashboard(cloneId, cloneProfile.getId(), mapDashboardIds);

        // copy config_area
        Map<Long, Long> mapAreaIds = new HashMap<>();
        this.copyConfigArea(mapDashboardIds, mapAreaIds);

        // copy config_map_chart_area
        this.copyMapChartArea(mapAreaIds);
        UserLogDto userLogDto = new UserLogDto("POST","COPY CAU_HINH_PROFILE",MessageUtil.getMessage("code.cau_hinh_profile.copy"),objectToJson(cloneId));
        userLogService.saveLog(userLogDto);
        return newConfigProfileDto;
    }

    private void copyProfileRole(Long cloneId, Long newId) {
        // find config_profile_role by profile_id
        List<ConfigProfileRoleEntity> entities = configProfileRoleRepository.findByProfileId(cloneId);
        if (entities.size() != 0) {
            List<ConfigProfileRoleDto> dtos = super.mapList(entities, ConfigProfileRoleDto.class);
            dtos.forEach(dto -> {
                dto.setId(null);
                dto.setProfileId(newId);
                dto.setUpdateTime(new Date());
                dto.setUpdateUser(super.getCurrentUsername());
            });
            entities = super.mapList(dtos, ConfigProfileRoleEntity.class);
            List<ConfigProfileRoleEntity> newCPRES = configProfileRoleRepository.saveAll(entities);
            newCPRES.forEach(e ->
                    logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_PROFILE_ROLE, Const.ACTION.INSERT,
                            e.getId(), null, e))
            );
        }
    }

    private void copyConfigMenu(Long cloneId, Long newId, Map<Long, Long> mapMenuIds) {
        // find config_menu by profile_id
        List<ConfigMenuEntity> menuEntities = configMenuRepository.findByProfileId(cloneId);
        if (!menuEntities.isEmpty()) {
            List<ConfigMenuDto> dtos = super.mapList(menuEntities, ConfigMenuDto.class);
            dtos.forEach(dto -> {
                Long oldMenuId = dto.getId();
                dto.setId(null);
                dto.setProfileId(newId);
                dto.setUpdateTime(new Date());
                dto.setUpdateUser(super.getCurrentUsername());

                ConfigMenuEntity newConfigMenuEntity = configMenuRepository.save(super.map(dto, ConfigMenuEntity.class));
                String newMenuName = newConfigMenuEntity.getMenuName() + "_" + newConfigMenuEntity.getId().intValue();
                newConfigMenuEntity.setMenuName(newMenuName);
                configMenuRepository.save(newConfigMenuEntity);
                mapMenuIds.put(oldMenuId, newConfigMenuEntity.getId());

                logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_MENU, Const.ACTION.INSERT,
                        newConfigMenuEntity.getId(), null, newConfigMenuEntity));
            });
        }
    }

    private void copyConfigMenuItems(Map<Long, Long> mapMenuIds) {
        mapMenuIds.forEach((oldId, newId) -> {
            List<ConfigMenuItemEntity> menuItemEntities = configMenuItemRepository.findByMenuId(oldId);
            if (!menuItemEntities.isEmpty()) {
                List<ConfigMenuItemDto> dtos = super.mapList(menuItemEntities, ConfigMenuItemDto.class);
                dtos.forEach(dto -> {
                    dto.setId(null);
                    dto.setMenuId(newId);
                    dto.setUpdateTime(new Date());
                    dto.setUpdateUser(super.getCurrentUsername());

                    ConfigMenuItemEntity newConfigMenuItemEntity = configMenuItemRepository
                            .save(super.map(dto, ConfigMenuItemEntity.class));
                    String newMenuItemName = newConfigMenuItemEntity.getMenuItemName() + "_" + newConfigMenuItemEntity.getId().intValue();
                    newConfigMenuItemEntity.setMenuItemName(newMenuItemName);
                    configMenuItemRepository.save(newConfigMenuItemEntity);

                    logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_MENU_ITEM, Const.ACTION.INSERT,
                            newConfigMenuItemEntity.getId(), null, newConfigMenuItemEntity));
                });
            }
        });
    }

    private void copyDashboard(Long cloneId, Long newId, Map<Long, Long> mapDashboardIds) {
        List<ConfigDashboardEntity> dashboardEntities = configDashBoardRepository.findByProfileId(cloneId);
//        List<ConfigDashboardEntity> newDashboardEntities = new ArrayList<>();
        if (dashboardEntities.size() != 0) {
            List<ConfigDashboardDto> dtos = super.mapList(dashboardEntities, ConfigDashboardDto.class);
            dtos.forEach(dto -> {
                Long oldId = dto.getId();
                dto.setId(null);
                dto.setProfileId(newId);
                dto.setUpdateTime(new Date());
                dto.setUpdateUser(super.getCurrentUsername());

                ConfigDashboardEntity newEntity = configDashBoardRepository.save(super.map(dto, ConfigDashboardEntity.class));
                String newDashboardName = newEntity.getDashboardName() + "_" + newEntity.getId().intValue();
                newEntity.setDashboardName(newDashboardName);
                configDashBoardRepository.save(newEntity);
                mapDashboardIds.put(oldId, newEntity.getId());

                logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_DASHBOARD, Const.ACTION.INSERT,
                        newEntity.getId(), null, newEntity));
            });
        }
    }

    private void copyConfigArea(Map<Long, Long> mapDashboardIds, Map<Long, Long> mapAreaIds) {
        mapDashboardIds.forEach((oldId, newId) -> {
            List<ConfigAreaEntity> areaEntities = configAreaRepository.findByDashboardId(oldId);
            if (areaEntities.size() != 0) {
                List<ConfigAreaDto> areaDtos = super.mapList(areaEntities, ConfigAreaDto.class);
                areaDtos.forEach(dto -> {
                    Long oldAreaId = dto.getId();
                    dto.setId(null);
                    dto.setDashboardId(newId);
                    dto.setUpdateTime(new Date());
                    dto.setUpdateUser(super.getCurrentUsername());

                    ConfigAreaEntity newEntity = configAreaRepository.save(super.map(dto, ConfigAreaEntity.class));
                    String newAreaName = newEntity.getAreaName() + "_" + newEntity.getId().intValue();
                    newEntity.setAreaName(newAreaName);
                    configAreaRepository.save(newEntity);
                    mapAreaIds.put(oldAreaId, newEntity.getId());

                    logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_AREA, Const.ACTION.INSERT,
                            newEntity.getId(), null, newEntity));
                });
            }
        });
    }

    private void copyMapChartArea(Map<Long, Long> mapAreaIds) {
        mapAreaIds.forEach((oldId, newId) -> {
            List<ConfigMapChartAreaEntity> mapChartAreaEntities = configMapChartAreaRepository.findByAreaId(oldId);
            if (mapChartAreaEntities.size() != 0) {
                List<ConfigMapChartAreaDto> mapChartAreaDtos = super.mapList(mapChartAreaEntities, ConfigMapChartAreaDto.class);
                mapChartAreaDtos.forEach(dto -> {
                    dto.setId(null);
                    dto.setAreaId(newId);
                    dto.setUpdateTime(new Date());
                    dto.setUpdateUser(super.getCurrentUsername());

                    Optional<ConfigAreaEntity> opt = configAreaRepository.findById(newId);
                    if (!opt.isPresent()) {
                        throw new ServerException(ErrorCode.NOT_FOUND);
                    }
                    dto.setDashboardIdNextto(opt.get().getDashboardId());

                    ConfigMapChartAreaEntity newEntity = configMapChartAreaRepository.save(super.map(dto, ConfigMapChartAreaEntity.class));

                    logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_MAP_CHART_AREA, Const.ACTION.INSERT,
                            newEntity.getId(), null, newEntity));
                });
            }
        });
    }

    public void deleteConfigProfileRole(Long id) {
        configProfileRoleRepository.deleteById(id);
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_PROFILE_ROLE, Const.ACTION.DELETE,
                id, id, null));
    }

    public void deleteConfigMenu(ConfigMenuEntity configMenu) {
        configMenuItemRepository.findByMenuId(configMenu.getId())
                .forEach(this::deleteConfigMenuItem);
        configMenuRepository.delete(configMenu);
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_MENU, Const.ACTION.DELETE,
                configMenu.getId(), configMenu.getId(), null));
    }

    public void deleteConfigMenuItem(ConfigMenuItemEntity configMenuItem) {
        configMenuItemRepository.delete(configMenuItem);
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_MENU_ITEM, Const.ACTION.DELETE,
                configMenuItem.getId(), configMenuItem.getId(), null));
    }

    public void deleteConfigDashboard(ConfigDashboardEntity configDashboard) {
        configAreaRepository.findByDashboardId(configDashboard.getId())
                .forEach(this::deleteConfigArea);
        configMapChartAreaRepository.findByDashboardIdNextto(configDashboard.getId())
                .forEach(this::deleteConfigMapChartArea);
        configDashBoardRepository.delete(configDashboard);
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_DASHBOARD, Const.ACTION.DELETE,
                configDashboard.getId(), configDashboard.getId(), null));
    }

    public void deleteConfigArea(ConfigAreaEntity configArea) {
        configMapChartAreaRepository.findByAreaId(configArea.getId())
                .forEach(this::deleteConfigMapChartArea);
        configAreaRepository.delete(configArea);
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_AREA, Const.ACTION.DELETE,
                configArea.getId(), configArea.getId(), null));
    }

    public void deleteConfigMapChartArea(ConfigMapChartAreaEntity configMapChartArea) {
        configMapChartAreaRepository.delete(configMapChartArea);
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.CONFIG_MAP_CHART_AREA, Const.ACTION.DELETE,
                configMapChartArea.getId(), configMapChartArea.getId(), null));
    }

    @Override
    public List<ConfigProfileDto> getAllConfigProfilesAndOrderBy(ConfigProfileDto configProfileDto, Pageable pageable) {
        return configProfileRepository.getAllConfigProfilesAndOrderBy(configProfileDto, super.getCurrentUsername(), super.getCurrentUserDeptId(), pageable);
    }
}
