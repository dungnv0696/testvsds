package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.ServiceGBTDDefineDto;
import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.ServiceGBTDDefineEntity;
import com.lifesup.gbtd.repository.CatDepartmentRepository;
import com.lifesup.gbtd.repository.ServiceGBTDDefineRepository;
import com.lifesup.gbtd.service.inteface.ILogActionService;
import com.lifesup.gbtd.service.inteface.IServiceGBTDDefine;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.birt.report.data.adapter.i18n.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ServiceGBTDDefine extends BaseService implements IServiceGBTDDefine {

    public ServiceGBTDDefine(ServiceGBTDDefineRepository serviceGBTDDefineRepository, ILogActionService logActionService,
                             CatDepartmentRepository catDepartmentRepository, UserLogService userLogService) {
        this.serviceGBTDDefineRepository = serviceGBTDDefineRepository;
        this.logActionService = logActionService;
        this.catDepartmentRepository = catDepartmentRepository;
        this.userLogService = userLogService;
    }

    private final ServiceGBTDDefineRepository serviceGBTDDefineRepository;
    private final ILogActionService logActionService;
    private final CatDepartmentRepository catDepartmentRepository;
    private final UserLogService userLogService;

    @Override
    public List<ServiceGBTDDefineDto> getAllDefineService(Long serviceId, Long deptId) {
        if (serviceId == null || deptId == null){
            throw new ServerException(ErrorCode.MISSING_PARAMS);
        }
        List<ServiceGBTDDefineDto> dtos = super.mapList(serviceGBTDDefineRepository
                        .findByServiceIdAndDeptId(serviceId, deptId), ServiceGBTDDefineDto.class);
        dtos.forEach(dto -> {
            List<Long> timetypes = new ArrayList<>();
            timetypes.add(dto.getTimeType());
            dto.setTimeTypes(timetypes);
        });
        return dtos;
    }

    @Override
    @Transactional
    public void add(List<ServiceGBTDDefineDto> dtos) {
        AtomicInteger count = new AtomicInteger(0);
        List<ServiceGBTDDefineEntity> entities = new ArrayList<>();
        Set<String> keys = new HashSet<>();
        dtos.forEach(dto -> {
            dto.getTimeTypes().forEach(timeType -> {
                dto.setTimeType(timeType);
                this.checkExistKeys(dto);
                keys.add(dto.getDeptId() + "_" + dto.getServiceId() + "_" + dto.getTimeType());
                count.getAndIncrement();

                ServiceGBTDDefineEntity entity = map(dto, ServiceGBTDDefineEntity.class);
                entity.setUpdateTime(new Date(System.currentTimeMillis()));
                entity.setUpdateUser(super.getCurrentUsername());
                entities.add(entity);
            });
        });

        if (count.intValue() == keys.size()) {
            // Save all dto to Db
            serviceGBTDDefineRepository.saveAll(entities);
        } else {
            throw new ServerException(ErrorCode.ALREADY_EXIST, "service");
        }
        UserLogDto userLogDto = new UserLogDto("POST","CREATE DEFINE_CHI_TIEU", MessageUtil.getMessage("code.define_chi_tieu.create"),objectToJson(dtos));
        userLogService.saveLog(userLogDto);
    }

    @Override
    @Transactional
    public void update(List<ServiceGBTDDefineDto> dtos) {
        Map<String, ServiceGBTDDefineEntity> entityMap = new HashMap<>();
        Map<String, String> entityMapOld = new HashMap<>();
        dtos.forEach(dto -> {
            if (dto.getTimeTypes() == null || dto.getTimeTypes().isEmpty() || Objects.isNull(dto.getDeptId())) {
                throw new ServerException(ErrorCode.MISSING_PARAMS);
            }
            dto.getTimeTypes().forEach(timeType -> {
                dto.setTimeType(timeType);
                this.checkExistKeys(dto);
                ServiceGBTDDefineEntity entity;
                String oldValue = null;
                if (Objects.nonNull(dto.getId())) {
                    entity = serviceGBTDDefineRepository.findById(dto.getId())
                            .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "serviceDefine"));
                    if (entity.equals(super.map(dto, ServiceGBTDDefineEntity.class))) return;
                    oldValue = super.toJson(entity);
                    super.mapData(dto, entity);
                } else {
                    //keys.add(dto.getDeptId() + "_" + dto.getServiceId() + "_" + dto.getTimeType());
                    entity = map(dto, ServiceGBTDDefineEntity.class);
                }

                entity.setUpdateTime(new Date());
                entity.setUpdateUser(super.getCurrentUsername());
//            keys.add(dto.getDeptId() + "_" + dto.getServiceId() + "_" + dto.getTimeType());
//            entities.add(entity);
                String key = dto.getDeptId() + "_" + dto.getServiceId() + "_" + dto.getTimeType();
                if (entityMap.containsKey(key)) {
                    throw new ServerException(ErrorCode.ALREADY_EXIST, "service");
                }

                entityMap.put(key, entity);
                entityMapOld.put(key, oldValue);
            });
        });
        // delete all record by serviceId and insert again
        //serviceGBTDDefineRepository.deleteByServiceIdAndDeptId(dtos.get(0).getServiceId(), dtos.get(0).getDeptId());
        serviceGBTDDefineRepository.saveAll(entityMap.values());
        for (Map.Entry<String, ServiceGBTDDefineEntity> e : entityMap.entrySet()) {
            String action = StringUtils.isEmpty(entityMapOld.get(e.getKey())) ? Const.ACTION.INSERT : Const.ACTION.UPDATE;
            logActionService.saveLogActionInternal(super.createLogDto(
                    Const.TABLE.SERVICE_GBTD_DEFINE, action, e.getValue().getId(), entityMapOld.get(e.getKey()), e.getValue()));
        }
        UserLogDto userLogDto = new UserLogDto("POST","UPDATE DINH_NGHIA_CHI_TIEU",MessageUtil.getMessage("code.define_chi_tieu.update"),objectToJson(dtos));
        userLogService.saveLog(userLogDto);
    }

    @Override
    @Transactional
    public void delete(ServiceGBTDDefineDto dto) {
        ServiceGBTDDefineEntity entity = serviceGBTDDefineRepository
                .findAllByServiceIdAndDeptIdAndTimeType(dto.getServiceId(), dto.getDeptId(), dto.getTimeType());
        serviceGBTDDefineRepository.delete(entity);
                //serviceGBTDDefineRepository.deleteByServiceIdAndDeptIdAndTimeType(dto.getServiceId(), dto.getDeptId(), dto.getTimeType());
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.SERVICE_GBTD_DEFINE, Const.ACTION.DELETE,
                entity.getId(), entity, null));
        UserLogDto userLogDto = new UserLogDto("POST","DELETE DEFINE_CHI_TIEU", MessageUtil.getMessage("code.define_chi_tieu.delete"),objectToJson(dto));
        userLogService.saveLog(userLogDto);
    }

    private void checkExistKeys(ServiceGBTDDefineDto dto) {
        ServiceGBTDDefineEntity result = serviceGBTDDefineRepository.findAllByServiceIdAndDeptIdAndTimeType(
                dto.getServiceId(), dto.getDeptId(), dto.getTimeType());
        if (Objects.nonNull(result)) {
            // Truong hop add
            if (Objects.isNull(dto.getId())) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, "service");
            }

            if (dto.getId().intValue() != result.getId().intValue()) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, "service");
            }
        };
    }
}
