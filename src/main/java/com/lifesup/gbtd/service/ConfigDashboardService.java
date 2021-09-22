package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.ConfigAreaEntity;
import com.lifesup.gbtd.model.ConfigDashboardEntity;
import com.lifesup.gbtd.model.ConfigMapChartAreaEntity;
import com.lifesup.gbtd.repository.ConfigAreaRepository;
import com.lifesup.gbtd.repository.ConfigDashboardRepository;
import com.lifesup.gbtd.repository.ConfigMapChartAreaRepository;
import com.lifesup.gbtd.repository.ConfigMenuItemRepository;
import com.lifesup.gbtd.repository.ConfigProfileRepository;
import com.lifesup.gbtd.repository.ConfigProfileRoleRepository;
import com.lifesup.gbtd.service.inteface.IConfigDashboardService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ConfigDashboardService extends BaseService implements IConfigDashboardService {

    private final ConfigDashboardRepository configDashboardRepository;
    private final ConfigAreaRepository configAreaRepository;
    private final ConfigMapChartAreaRepository configMapChartAreaRepository;
    private final ConfigMenuItemRepository configMenuItemRepository;
    private final ConfigAreaService configAreaService;
    private final ConfigProfileRepository configProfileRepository;
    private final ConfigProfileRoleRepository configProfileRoleRepository;
    private final UserLogService userLogService;

    @Autowired
    public ConfigDashboardService(ConfigDashboardRepository configDashboardRepository,
                                  ConfigAreaRepository configAreaRepository,
                                  ConfigMapChartAreaRepository configMapChartAreaRepository,
                                  ConfigMenuItemRepository configMenuItemRepository,
                                  ConfigAreaService configAreaService,
                                  ConfigProfileRepository configProfileRepository,
                                  ConfigProfileRoleRepository configProfileRoleRepository, UserLogService userLogService) {
        this.configDashboardRepository = configDashboardRepository;
        this.configAreaRepository = configAreaRepository;
        this.configMapChartAreaRepository = configMapChartAreaRepository;
        this.configMenuItemRepository = configMenuItemRepository;
        this.configAreaService = configAreaService;
        this.configProfileRepository = configProfileRepository;
        this.configProfileRoleRepository = configProfileRoleRepository;
        this.userLogService = userLogService;
    }

    @Override
    public Page<ConfigDashboardDto> doSearch(ConfigDashboardDto dto, Pageable pageable) {
        Page<ConfigDashboardDto> data = configDashboardRepository.doSearch(dto, pageable);
        List<Long> menuItemIds = data.getContent().stream().map(ConfigDashboardDto::getMenuItemId).collect(Collectors.toList());
        List<ConfigMenuItemDto> menuItems = super.mapList(configMenuItemRepository.findAllById(menuItemIds), ConfigMenuItemDto.class);
        List<ConfigDashboardDto> content = data.getContent().stream().peek(
                e -> menuItems.stream()
                        .filter(m -> m.getId().equals(e.getMenuItemId())).findFirst().ifPresent(e::setMenuItem)
        ).collect(Collectors.toList());
//        UserLogDto userLogDto = new UserLogDto("GET","SEARCH CAU_HINH_DASHBOARD","Tìm kiếm mới cấu hình dashboard");
//        userLogService.saveLog(userLogDto);
        return new PageImpl<>(content, data.getPageable(), data.getTotalElements());
    }

    @Override
    public ConfigDashboardDto findById(Long id) {
        return this.findDashboardWithFilter(id, null);
    }

    private void validateSave(ConfigDashboardDto dto) {
        if (Objects.nonNull(dto.getId())) {
            throw new ServerException(ErrorCode.ALREADY_EXIST, "dashboard");
        }
        if (Const.DASHBOARD_TYPE.DEFAULT.equals(dto.getDashboardType())) {
            List<ConfigDashboardEntity> list = configDashboardRepository.findByProfileIdAndDashboardType(dto.getProfileId(), Const.DASHBOARD_TYPE.DEFAULT);
            if (!DataUtil.isNullOrEmpty(list)) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, "dashboard default");
            }
        }
    }

    private void validateUpdate(ConfigDashboardDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "id");
        }
        if (Const.DASHBOARD_TYPE.DEFAULT.equals(dto.getDashboardType())) {
            List<ConfigDashboardEntity> list = configDashboardRepository.findByProfileIdAndDashboardType(dto.getProfileId(), Const.DASHBOARD_TYPE.DEFAULT);
            if (!DataUtil.isNullOrEmpty(list) && list.stream().noneMatch(e -> Objects.equals(e.getId(), dto.getId()))) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, "dashboard default");
            }
        }
    }

    @Override
    public ConfigDashboardDto create(ConfigDashboardDto dto) {
        this.validateSave(dto);
        dto.setStatus(super.prepareStatus(dto.getStatus()));
        dto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.INSERT));
        UserLogDto userLogDto = new UserLogDto("POST","CREATE CAU_HINH_DASHBOARD", MessageUtil.getMessage("code.cau_hinh_dashboard.create"),objectToJson(dto));
        userLogService.saveLog(userLogDto);
        return this.save(dto);
    }

    @Override
    public ConfigDashboardDto update(ConfigDashboardDto dto) {
        this.validateUpdate(dto);
        dto.setStatus(super.prepareStatus(dto.getStatus()));
        dto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.UPDATE));
        UserLogDto userLogDto = new UserLogDto("POST","UPDATE CAU_HINH_DASHBOARD",MessageUtil.getMessage("code.cau_hinh_dashboard.update"),objectToJson(dto));
        userLogService.saveLog(userLogDto);
        return this.save(dto);
    }

    @Override
    public void delete(Long id) {
        List<ActionAuditDto> actionLogs = new ArrayList<>();
        configAreaRepository.findByDashboardId(id).forEach(ca -> {
            configMapChartAreaRepository.findByAreaId(ca.getId()).forEach(mc -> {
                actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_MAP_CHART_AREA, mc.getId(), mc));
                configMapChartAreaRepository.delete(mc);
            });
            actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_AREA, ca.getId(), ca));
            configAreaRepository.delete(ca);
        });
        ConfigDashboardEntity dashboard = configDashboardRepository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.CONFIG_DASHBOARD));
        actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_DASHBOARD, id, dashboard));
        configDashboardRepository.delete(dashboard);
        UserLogDto userLogDto = new UserLogDto("POST","DELETE CAU_HINH_DASHBOARD",MessageUtil.getMessage("code.cau_hinh_dashboard.delete"),objectToJson(id));
        userLogService.saveLog(userLogDto);
        super.saveLog(actionLogs);
    }

    public ConfigDashboardDto save(ConfigDashboardDto dto) {
        List<ActionAuditDto> actionLogs = new ArrayList<>();
        dto.setUpdateTime(new Date());
        dto.setUpdateUser(super.getCurrentUsername());
        ActionAuditDto.Builder logDashBoard = dto.getLogBuilder().tableName(Const.TABLE.CONFIG_DASHBOARD).oldValue(null != dto.getId() ? configDashboardRepository.findById(dto.getId()).orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND)) : null);
        ConfigDashboardEntity entity = configDashboardRepository.save(super.map(dto, ConfigDashboardEntity.class));
        ConfigDashboardDto dtoReturn = super.map(entity, ConfigDashboardDto.class);
        actionLogs.add(logDashBoard.newValue(entity).objectId(entity.getId()).build());
        List<ConfigAreaEntity> existed = configAreaRepository.findByDashboardId(entity.getId());
        List<Long> existedIds = existed.stream().map(ConfigAreaEntity::getId).collect(Collectors.toList());
        List<ConfigAreaDto> areas = dto.getConfigAreaDtos();
        List<ConfigAreaDto> needSaveArea = areas.stream().filter(i -> i.getId() == null || existedIds.contains(i.getId())).collect(Collectors.toList());
        List<Long> needUpdateIds = needSaveArea.stream().filter(i -> i.getId() != null).map(ConfigAreaDto::getId).collect(Collectors.toList());
        List<ConfigAreaEntity> needDeleteArea = existed.stream().filter(i -> !needUpdateIds.contains(i.getId())).collect(Collectors.toList());
        this.deleteAreaAndMapChart(needDeleteArea, actionLogs);
        if (!DataUtil.isNullOrEmpty(needSaveArea)) {
            needSaveArea = needSaveArea.stream().peek(i -> {
                i.setDashboardId(dtoReturn.getId());
                i.setUpdateTime(new Date());
                i.setUpdateUser(super.getCurrentUsername());
                List<ConfigMapChartAreaDto> mapChart = i.getMapCharts();
                ConfigAreaEntity saved;
                if (needUpdateIds.contains(i.getId())) {
                    saved = configAreaRepository.findById(i.getId())
                            .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.CONFIG_AREA));
                    String oldValue = super.toJson(saved);
                    saved = configAreaRepository.save(super.map(i, ConfigAreaEntity.class));
                    actionLogs.add(super.updateLog(Const.TABLE.CONFIG_AREA, saved.getId(), oldValue, saved));
                } else {
                    saved = configAreaRepository.save(super.map(i, ConfigAreaEntity.class));
                    actionLogs.add(super.insertLog(Const.TABLE.CONFIG_AREA, saved.getId(), saved));
                }
                i.setId(saved.getId());
                if (!DataUtil.isNullOrEmpty(mapChart)) {
                    mapChart = mapChart.stream().peek(m -> m.setAreaId(i.getId())).collect(Collectors.toList());
                    i.setMapCharts(mapChart);
                }
            }).collect(Collectors.toList());
            List<Long> newAreaIds = needSaveArea.stream().map(ConfigAreaDto::getId).collect(Collectors.toList());
            if (!DataUtil.isNullOrEmpty(newAreaIds)) {
                List<ConfigMapChartAreaEntity> mapChartExists = configMapChartAreaRepository.findByAreaIdIn(newAreaIds);
                mapChartExists.forEach(mce -> {
                    actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_MAP_CHART_AREA, mce.getId(), mce));
                    configMapChartAreaRepository.delete(mce);
                });
            }
            List<ConfigMapChartAreaDto> mapCharts = needSaveArea.stream().map(ConfigAreaDto::getMapCharts).flatMap(List::stream).collect(Collectors.toList());
            if (!DataUtil.isNullOrEmpty(mapCharts)) {
                mapCharts.forEach(mc -> {
                    mc.setStatus(Const.STATUS.ACTIVE);
                    mc.setUpdateTime(new Date());
                    mc.setUpdateUser(super.getCurrentUsername());
                    ConfigMapChartAreaEntity saved = configMapChartAreaRepository.save(super.map(mc, ConfigMapChartAreaEntity.class));
                    actionLogs.add(super.insertLog(Const.TABLE.CONFIG_MAP_CHART_AREA, saved.getId(), saved));
                });
            }
        }
        dtoReturn.setConfigAreaDtos(needSaveArea);
        super.saveLog(actionLogs);
        return dtoReturn;
    }

    @Override
    @Transactional
    public List<ConfigDashboardDto> findAll(String keyword, Long[] profileIds, Long[] menuIds, Long[] menuItemIds, Long isDefault, Long status) {
        log.debug("Request to get list ConfigMapChartArea");
        return super.mapList(configDashboardRepository.findAll(keyword, profileIds, menuIds, menuItemIds,
                isDefault, status), ConfigDashboardDto.class);
    }

    @Override
    public ConfigDashboardDto copy(Long id) {
        ConfigDashboardDto old = this.findById(id);
        old.setId(null);
        old.setDashboardName(old.getDashboardName() + " Copy");
        old.setDashboardType(Objects.equals(old.getDashboardType(), Const.DASHBOARD_TYPE.DEFAULT)
                ? Const.DASHBOARD_TYPE.DETAIL
                : old.getDashboardType());
        old.setMenuItemId(null);

        old.getConfigAreaDtos().forEach(e -> {
            e.setId(null);
            if (!DataUtil.isNullOrEmpty(e.getMapCharts())) {
                e.getMapCharts().forEach(mc -> mc.setId(null));
            }
        });
        old.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.INSERT));
        UserLogDto userLogDto = new UserLogDto("GET","COPY CAU_HINH_DASHBOARD",MessageUtil.getMessage("code.cau_hinh_dashboard.copy"),objectToJson(id));
        userLogService.saveLog(userLogDto);
        return this.save(old);
    }

    @Override
    public List<CatItemDto> getTimeTypeByServiceId(Long[] serviceIds) {
        return configDashboardRepository.getTimeTypeByServiceId(serviceIds);
    }

    public void deleteAreaAndMapChart(List<ConfigAreaEntity> needDeleteArea, List<ActionAuditDto> actionLogs) {
        if (!DataUtil.isNullOrEmpty(needDeleteArea)) {
            List<Long> areaIdsDelete = new ArrayList<>();
            needDeleteArea.forEach(e -> {
                areaIdsDelete.add(e.getId());
                actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_AREA, e.getId(), e));
                configAreaRepository.delete(e);
            });

            List<ConfigMapChartAreaEntity> mapChartAreaEntities = configMapChartAreaRepository.findByAreaIdIn(areaIdsDelete);
            mapChartAreaEntities.forEach(e -> {
                actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_MAP_CHART_AREA, e.getId(), e));
                configMapChartAreaRepository.delete(e);
            });
        }
    }

    @Override
    public ConfigDashboardDto findDashboardWithFilter(Long id, ConfigDashboardDto filter) {
        ConfigDashboardDto dashboard = configDashboardRepository.findById(id)
                .map(e -> super.map(e, ConfigDashboardDto.class))
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "dashboard"));
         // Check can access
        if ((null == configProfileRoleRepository.findByProfileIdAndDeptId(dashboard.getProfileId(), super.getCurrentUserDeptId()))
        && (null == configProfileRoleRepository.findByProfileIdAndUsernameUsed(dashboard.getProfileId(), super.getCurrentUsername()))

        ) {
            throw new ServerException(ErrorCode.ACCESS_DENIED, "Cannot access dashboard!");
        }

        if (!Const.STATUS.ACTIVE.equals(dashboard.getStatus())) {
            throw new ServerException(ErrorCode.NOT_FOUND, "dashboard disabled");
        }
        if (Objects.nonNull(dashboard.getMenuItemId())) {
            ConfigMenuItemDto menuItem = configMenuItemRepository.findById(dashboard.getMenuItemId())
                    .map(e -> super.map(e, ConfigMenuItemDto.class))
                    .orElse(null);
            dashboard.setMenuItem(menuItem);
        }

        if (null == filter) {
            filter = new ConfigDashboardDto(id);
        } else {
            filter.setId(id);
        }

        dashboard.setConfigAreaDtos(this.filterChartInArea(filter));

        if (!dashboard.getConfigAreaDtos().isEmpty()) {
            dashboard.setTotalPageConfigArea(
                    Collections.max(
                            dashboard.getConfigAreaDtos(),
                            Comparator.comparing(c -> null != c.getPageDashboard() ? c.getPageDashboard() : 1))
                            .getPageDashboard());
        }

        if (Objects.nonNull(dashboard.getProfileId())) {
            if (configProfileRepository.getByDeptIdAndUsernameUsedAndIds(super.getCurrentUserDeptId(),
                    super.getCurrentUsername(), Arrays.asList(dashboard.getProfileId())).isEmpty()) {
                dashboard.setEditable(false);
            } else {
                dashboard.setEditable(true);
            }
        }

        return dashboard;
    }

    private List<ConfigAreaDto> filterChartInArea(ConfigDashboardDto filter) {
        return configAreaService.getByDashboardId(filter.getId(), filter);
    }
}
