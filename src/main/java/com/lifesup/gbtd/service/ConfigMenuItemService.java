package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.ActionAuditDto;
import com.lifesup.gbtd.dto.object.ConfigMenuItemDto;
import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.ConfigMenuItemEntity;
import com.lifesup.gbtd.repository.ConfigDashboardRepository;
import com.lifesup.gbtd.repository.ConfigMenuItemRepository;
import com.lifesup.gbtd.service.inteface.IConfigMenuItemService;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class ConfigMenuItemService extends BaseService implements IConfigMenuItemService {

    private final ConfigMenuItemRepository configMenuItemRepository;
    private final ConfigDashboardRepository configDashboardRepository;
    private final UserLogService userLogService;

    @Autowired
    public ConfigMenuItemService(ConfigMenuItemRepository configMenuItemRepository,
                                 ConfigDashboardRepository configDashboardRepository, UserLogService userLogService) {
        this.configMenuItemRepository = configMenuItemRepository;
        this.configDashboardRepository = configDashboardRepository;
        this.userLogService = userLogService;
    }

    @Override
    public List<ConfigMenuItemDto> findAll(String keyword, Long[] menuIds, Long isDefault, Long status) {
        return super.mapList(configMenuItemRepository.findAll(keyword, menuIds, isDefault, status),
                ConfigMenuItemDto.class);
    }

    @Override
    public Page<ConfigMenuItemDto> findAllByMenu(ConfigMenuItemDto dto, Pageable pageable) {
        if (Objects.isNull(dto.getMenuId())) {
            throw new ServerException(ErrorCode.NOT_FOUND);
        }
        List<ConfigMenuItemDto> dtos = super.mapList(configMenuItemRepository
                .findByMenuIdOrderByOrderIndex(dto.getMenuId(), pageable), ConfigMenuItemDto.class);
        dtos.forEach(item -> {
            item.setHaveDashboard(false);
            configDashboardRepository.findByMenuItemId(item.getId())
                    .stream()
                    .findAny()
                    .ifPresent(e -> {
                        item.setHaveDashboard(true);
                    });
        });
        Long count = configMenuItemRepository.countByMenuId(dto.getMenuId());
        return new PageImpl<>(dtos, pageable, count);
    }

    @Override
    @Transactional
    public List<ConfigMenuItemDto> updateConfigMenuItem(List<ConfigMenuItemDto> dtos) {
        List<ActionAuditDto> actionLogs = new ArrayList<>();
        if (dtos == null || dtos.isEmpty()) {
            throw new ServerException(ErrorCode.NOT_VALID);
        }
        List<ConfigMenuItemEntity> entities = new ArrayList<>();
        Set<String> itemNameSet = new HashSet<>();
        dtos.forEach(dto -> {
            if (dto.getMenuId() == null) {
                throw new ServerException(ErrorCode.MISSING_PARAMS, "MenuId");
            }
            this.checkExistKeys(dto);
            if (!itemNameSet.add(dto.getMenuItemName()))
                throw new ServerException(ErrorCode.ALREADY_EXIST, "MenuItemName");
            String oldValue = null;
            ConfigMenuItemEntity entity;
            if (Objects.nonNull(dto.getId())) {
                entity = configMenuItemRepository.findById(dto.getId())
                        .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND));
                if (entity.equals(super.map(dto, ConfigMenuItemEntity.class))) return;
                oldValue = super.toJson(entity);
                super.mapData(dto, entity);
            } else {
                entity = super.map(dto, ConfigMenuItemEntity.class);
            }
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(super.getCurrentUsername());

            entities.add(configMenuItemRepository.save(entity));
            if(Objects.nonNull(dto.getId())) {
                actionLogs.add(super.updateLog(Const.TABLE.CONFIG_MENU_ITEM, entity.getId(), oldValue, entity));
            } else {
                actionLogs.add(super.insertLog(Const.TABLE.CONFIG_MENU_ITEM, entity.getId(), entity));
            }
        });
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "UPDATE CONFIG_MENU_ITEM", "Sửa thẻ menu", objectToJson(dtos));
        userLogService.saveLog(userLogDto);
        super.saveLog(actionLogs);
        return super.mapList(entities, ConfigMenuItemDto.class);
    }

    @Override
    public void delete(Long id) {
        ConfigMenuItemEntity entity = configMenuItemRepository.findById(id)
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "configMenuItem"));
        Long orderIndex = entity.getOrderIndex();
        Long menuId = entity.getMenuId();
        super.saveLog(Collections.singletonList(super.deleteLog(Const.TABLE.CONFIG_MENU_ITEM, entity.getId(), entity)));
        configMenuItemRepository.delete(entity);
        configMenuItemRepository.findByMenuIdAndOrderIndexGreaterThan(menuId, orderIndex)
                .forEach(e -> {
                    String oldValue = super.toJson(e);
                    e.setOrderIndex(e.getOrderIndex() - 1);
                    e.setUpdateTime(new Date());
                    e.setUpdateUser(super.getCurrentUsername());
                    super.saveLog(Collections.singletonList(super.updateLog(Const.TABLE.CONFIG_MENU_ITEM,
                            e.getId(),
                            oldValue,
                            configMenuItemRepository.save(e))));
                });
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "DELETE CONFIG_MENU_ITEM", "Sửa thẻ menu", objectToJson(id));
        userLogService.saveLog(userLogDto);
    }

    private void checkExistKeys(ConfigMenuItemDto dto) {
        configMenuItemRepository.findByMenuItemNameAndMenuId(dto.getMenuItemName().trim(), dto.getMenuId())
                .stream()
                .findAny()
                .ifPresent(e -> {
                    if(Objects.isNull(dto.getId()) || !e.getId().equals(dto.getId()))
                        throw new ServerException(ErrorCode.ALREADY_EXIST, "MenuItemName");
                });
    }
}
