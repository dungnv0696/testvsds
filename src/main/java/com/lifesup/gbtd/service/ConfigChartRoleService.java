package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.ConfigChartRoleDto;
import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.ConfigChartRoleEntity;
import com.lifesup.gbtd.repository.ConfigChartRoleRepository;
import com.lifesup.gbtd.service.inteface.IConfigChartRoleService;
import com.lifesup.gbtd.service.inteface.ILogActionService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.birt.report.data.adapter.i18n.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ConfigChartRoleService extends BaseService implements IConfigChartRoleService {

    private ConfigChartRoleRepository configChartRoleRepository;
    private final ILogActionService logActionService;
    private final UserLogService userLogService;

    @Autowired
    public ConfigChartRoleService(ConfigChartRoleRepository configChartRoleRepository, ILogActionService logActionService, UserLogService userLogService) {
        this.configChartRoleRepository = configChartRoleRepository;
        this.logActionService = logActionService;
        this.userLogService = userLogService;
    }

    @Override
    public List<ConfigChartRoleDto> get(ConfigChartRoleDto dto, Pageable pageable) {
        return configChartRoleRepository.findAll(dto, pageable)
                .stream()
                .map(e -> super.map(e, ConfigChartRoleDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfigChartRoleDto> getByChartId(Long chartId) {
        return configChartRoleRepository.findByChartId(chartId).stream()
                .map(e -> super.map(e, ConfigChartRoleDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateConfigChartRole(List<ConfigChartRoleDto> dtos) {
        Map<String, ConfigChartRoleEntity> entityMap = new HashMap<>();
        Map<String, String> entityMapOld = new HashMap<>();
        AtomicInteger countRecAdmin = new AtomicInteger();
        if (dtos == null || dtos.isEmpty()) {
            throw new ServerException(ErrorCode.NOT_VALID);
        }
        if (Objects.isNull(dtos.get(0).getRoleType())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS);
        }

        dtos.forEach(dto -> {
            this.validateConfigChartRole(dto);
            if ("ADMIN".equals(dto.getRoleCode())) countRecAdmin.getAndIncrement();
            ConfigChartRoleEntity entity;
            String oldValue = null;
            if (Objects.nonNull(dto.getId())) {
                entity = configChartRoleRepository.findById(dto.getId())
                        .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND));
                if (entity.equals(super.map(dto, ConfigChartRoleEntity.class))) return;
                oldValue = super.toJson(entity);
                super.mapData(dto, entity);
            } else {
                entity = super.map(dto, ConfigChartRoleEntity.class);
            }
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(super.getCurrentUsername());
            String key = null;
            Long roleType = dtos.get(0).getRoleType();

            if (roleType == 1) {
                key = dto.getChartId() + "_" + dto.getDeptId();
            }

            if (roleType == 2) {
                key = dto.getChartId() + "_" + dto.getUsernameUsed();
            }

            if (entityMap.containsKey(key)) {
                throw new ServerException(ErrorCode.ALREADY_EXIST);
            }
            entityMap.put(key, entity);
            entityMapOld.put(key, oldValue);
        });

        if (countRecAdmin.intValue() == 0) {
            throw new ServerException(ErrorCode.ACCESS_DENIED);
        }

        configChartRoleRepository.saveAll(entityMap.values());
        for (Map.Entry<String, ConfigChartRoleEntity> e : entityMap.entrySet()) {
            String action = StringUtils.isEmpty(entityMapOld.get(e.getKey())) ? Const.ACTION.INSERT : Const.ACTION.UPDATE;
            logActionService.saveLogActionInternal(super.createLogDto(
                    Const.TABLE.CONFIG_CHART_ROLE, action, e.getValue().getId(), entityMapOld.get(e.getKey()), e.getValue()));
        }
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "UPDATE AUTHORIZE CAU_HINH_BIEU_DO", MessageUtil.getMessage("code.cau_hinh_bieu_do.authorize.update"), objectToJson(dtos));
        userLogService.saveLog(userLogDto);
    }

    @Override
    public void deleteConfigChartRole(Long id) {
        Optional<ConfigChartRoleEntity> opt = configChartRoleRepository.findById(id);
        if (!opt.isPresent()) {
            throw new ServerException(ErrorCode.NOT_FOUND);
        }
        ConfigChartRoleEntity entity = opt.get();
        if ("ADMIN".equals(entity.getRoleCode())) {
            List<ConfigChartRoleEntity> entities = configChartRoleRepository.findByChartIdAndRoleCodeAndIdIsNot(
                    entity.getChartId(), "ADMIN", entity.getId());
            if (entities.size() == 0) {
                throw new ServerException(ErrorCode.ACCESS_DENIED);
            }
        }

        configChartRoleRepository.deleteById(id);
        logActionService.saveLogActionInternal(super.createLogDto(
                Const.TABLE.CONFIG_CHART_ROLE, Const.ACTION.DELETE, id, opt.get(), null));
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "DELETE AUTHORIZE CAU_HINH_BIEU_DO", MessageUtil.getMessage("code.cau_hinh_bieu_do.authorize.delete"), objectToJson(id));
        userLogService.saveLog(userLogDto);
    }

    private void validateConfigChartRole(ConfigChartRoleDto dto) {
        if (Objects.isNull(dto.getChartId()) || (Objects.isNull(dto.getDeptId())
                && StringUtils.isEmpty(dto.getUsernameUsed())) || StringUtils.isEmpty(dto.getRoleCode())
                || Objects.isNull(dto.getRoleType())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS);
        }
        ConfigChartRoleEntity result = null;

        if (Objects.nonNull(dto.getDeptId())) {
            result = configChartRoleRepository.findByChartIdAndDeptId(dto.getChartId(),
                    dto.getDeptId());
        }

        if (Objects.nonNull(dto.getUsernameUsed())) {
            result = configChartRoleRepository.findByChartIdAndUsernameUsed(dto.getChartId(),
                    dto.getUsernameUsed());
        }

        if (result != null) {
            // Truong hop add
            if (Objects.isNull(dto.getId())) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, "configChartRole");
            }

            if (dto.getId().intValue() != result.getId().intValue()) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, "configChartRole");
            }
        }
    }
}
