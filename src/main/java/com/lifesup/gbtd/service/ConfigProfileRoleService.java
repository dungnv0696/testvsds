package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.ConfigProfileRoleDto;
import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.ConfigProfileRoleEntity;
import com.lifesup.gbtd.repository.ConfigProfileRoleRepository;
import com.lifesup.gbtd.service.inteface.IConfigProfileRoleService;
import com.lifesup.gbtd.service.inteface.ILogActionService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

@Service
public class ConfigProfileRoleService extends BaseService implements IConfigProfileRoleService {

    private final ConfigProfileRoleRepository configProfileRoleRepository;
    private final ILogActionService logActionService;
    private final UserLogService userLogService;


    @Autowired
    public ConfigProfileRoleService(ConfigProfileRoleRepository configProfileRoleRepository, ILogActionService logActionService, UserLogService userLogService) {
        this.configProfileRoleRepository = configProfileRoleRepository;
        this.logActionService = logActionService;
        this.userLogService = userLogService;
    }

    @Override
    @Transactional
    public void updateConfigProfileRole(List<ConfigProfileRoleDto> dtos) {
        Map<String, ConfigProfileRoleEntity> entityMap = new HashMap<>();
        Map<String, String> entityMapOld = new HashMap<>();
        AtomicInteger countRecAdmin = new AtomicInteger();
        if (dtos == null || dtos.isEmpty()) {
            throw new ServerException(ErrorCode.NOT_VALID);
        }
        if (Objects.isNull(dtos.get(0).getRoleType())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS);
        }

        dtos.forEach(dto -> {
            this.validateConfigProfileRole(dto);
            if ("ADMIN".equals(dto.getRoleCode())) countRecAdmin.getAndIncrement();
            ConfigProfileRoleEntity entity;
            String oldValue = null;
            if (Objects.nonNull(dto.getId())) {
                entity = configProfileRoleRepository.findById(dto.getId())
                        .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND));
                if (entity.equals(super.map(dto, ConfigProfileRoleEntity.class))) return;
                oldValue = super.toJson(entity);
                super.mapData(dto, entity);
            } else {
                entity = super.map(dto, ConfigProfileRoleEntity.class);
            }
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(super.getCurrentUsername());
            String key = null;
            Long roleType = dtos.get(0).getRoleType();
            if (roleType == 1) {
                key = dto.getProfileId() + "_" + dto.getDeptId();
            }

            if (roleType == 2) {
                key = dto.getProfileId() + "_" + dto.getUsernameUsed();
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

        configProfileRoleRepository.saveAll(entityMap.values());
        for (Map.Entry<String, ConfigProfileRoleEntity> e : entityMap.entrySet()) {
                String action = StringUtils.isEmpty(entityMapOld.get(e.getKey())) ? Const.ACTION.INSERT : Const.ACTION.UPDATE;
                logActionService.saveLogActionInternal(super.createLogDto(
                        Const.TABLE.CONFIG_PROFILE_ROLE, action, e.getValue().getId(), entityMapOld.get(e.getKey()), e.getValue()));
        }
        UserLogDto userLogDto = new UserLogDto("POST","UPDATE AUTHORIZE CAU_HINH_PROFILE", MessageUtil.getMessage("code.cau_hinh_profile.authorize.update"),objectToJson(dtos));
        userLogService.saveLog(userLogDto);

    }

    @Override
    @Transactional
    public void deleteConfigProfileRole(Long id) {
        Optional<ConfigProfileRoleEntity> opt = configProfileRoleRepository.findById(id);
        if (!opt.isPresent()) {
            throw new ServerException(ErrorCode.NOT_FOUND);
        }
        ConfigProfileRoleEntity entity = opt.get();
        if ("ADMIN".equals(entity.getRoleCode())) {
            List<ConfigProfileRoleEntity> entities = configProfileRoleRepository.findByProfileIdAndRoleCodeAndIdIsNot(
                    entity.getProfileId(), "ADMIN", entity.getId());
            if (entities.size() == 0) {
                throw new ServerException(ErrorCode.ACCESS_DENIED);
            }
        }

        configProfileRoleRepository.deleteById(id);
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST","DELETE AUTHORIZE CAU_HINH_PROFILE", MessageUtil.getMessage("code.cau_hinh_profile.authorize.delete"),objectToJson(id));
        userLogService.saveLog(userLogDto);
        logActionService.saveLogActionInternal(super.createLogDto(
                Const.TABLE.CONFIG_PROFILE_ROLE, Const.ACTION.DELETE, id, opt.get(), null));
    }

    @Override
    public Page<ConfigProfileRoleDto> findProfileRoles(Long profileId, Pageable pageable) {
        if (Objects.isNull(profileId)) {
            throw new ServerException(ErrorCode.NOT_FOUND);
        }
        List<ConfigProfileRoleDto> dtos = super.mapList(configProfileRoleRepository.findByProfileId(profileId, pageable)
                , ConfigProfileRoleDto.class);
        Long count = configProfileRoleRepository.countByProfileId(profileId);
        //ghi log
//        UserLogDto userLogDto = new UserLogDto("GET","SEARCH AUTHORIZE CAU_HINH_PROFILE","Tìm kiếm phân quyền cấu hình profile");
//        userLogService.saveLog(userLogDto);
        return new PageImpl<>(dtos, pageable, count);
    }

    private void validateConfigProfileRole(ConfigProfileRoleDto dto) {
        if (Objects.isNull(dto.getProfileId()) || (Objects.isNull(dto.getDeptId())
                && StringUtils.isEmpty(dto.getUsernameUsed())) || StringUtils.isEmpty(dto.getRoleCode())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS);
        }
        ConfigProfileRoleEntity result = null;

        if (Objects.nonNull(dto.getDeptId())) {
            result = configProfileRoleRepository.findByProfileIdAndDeptId(dto.getProfileId(),
                    dto.getDeptId());
        }

        if (Objects.nonNull(dto.getUsernameUsed())) {
            result = configProfileRoleRepository.findByProfileIdAndUsernameUsed(dto.getProfileId(),
                    dto.getUsernameUsed());
        }

        if (result != null) {
            // Truong hop add
            if (Objects.isNull(dto.getId())) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, "configProfileRole");
            }

            if (dto.getId().intValue() != result.getId().intValue()) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, "configProfileRole");
            }
        }
    }
}
