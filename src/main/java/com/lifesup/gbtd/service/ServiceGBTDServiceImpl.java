package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.BiTdServicesTreeEntity;
import com.lifesup.gbtd.model.ServiceGBTDDefineEntity;
import com.lifesup.gbtd.model.ServiceGBTDEntity;
import com.lifesup.gbtd.model.ServicesMapDeptEntity;
import com.lifesup.gbtd.repository.BiTdServicesTreeRepository;
import com.lifesup.gbtd.repository.ServiceGBTDDefineRepository;
import com.lifesup.gbtd.repository.ServiceGBTDRepository;
import com.lifesup.gbtd.repository.ServicesMapDeptRepository;
import com.lifesup.gbtd.repository.UsersRepository;
import com.lifesup.gbtd.service.inteface.ICatUnitService;
import com.lifesup.gbtd.service.inteface.ILogActionService;
import com.lifesup.gbtd.service.inteface.IServiceGBTDService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServiceGBTDServiceImpl extends BaseService implements IServiceGBTDService {

    private final ServiceGBTDRepository serviceGBTDRepository;
    private final ServicesMapDeptRepository servicesMapDeptRepository;
    private final ServiceGBTDDefineRepository serviceGBTDDefineRepository;
    private final BiTdServicesTreeRepository biTdServicesTreeRepository;
    private final ILogActionService logActionService;
    private final ICatUnitService catUnitService;
    private final UsersRepository usersRepository;
    private final UserLogService userLogService;

    @Autowired
    public ServiceGBTDServiceImpl(ServiceGBTDRepository serviceGBTDRepository,
                                  ServicesMapDeptRepository servicesMapDeptRepository,
                                  ServiceGBTDDefineRepository serviceGBTDDefineRepository,
                                  BiTdServicesTreeRepository biTdServicesTreeRepository,
                                  ILogActionService logActionService,
                                  ICatUnitService catUnitService,
                                  UsersRepository usersRepository, UserLogService userLogService) {
        this.serviceGBTDRepository = serviceGBTDRepository;
        this.servicesMapDeptRepository = servicesMapDeptRepository;
        this.serviceGBTDDefineRepository = serviceGBTDDefineRepository;
        this.biTdServicesTreeRepository = biTdServicesTreeRepository;
        this.logActionService = logActionService;
        this.catUnitService = catUnitService;
        this.usersRepository = usersRepository;
        this.userLogService = userLogService;
    }

    @Override
    public List<ServiceGBTDDto> findServiceGBTDs(ServiceGBTDDto criteria) {
//        UserLogDto userLogDto = new UserLogDto("POST","SEARCH DANH_MUC_CHI_TIEU","Tìm kiếm danh mục chỉ tiêu",objectToJson(criteria));
//        userLogService.saveLog(userLogDto);
        List<ServiceGBTDDto> dtos = serviceGBTDRepository.findServiceGBTDs(criteria, super.getCurrentUsername());
        dtos.forEach(dto -> {
            List<ServicesMapDeptDto> depts = super.mapList(servicesMapDeptRepository.findByServiceId(dto.getServiceId()),
                    ServicesMapDeptDto.class);
            List<Long> deptIds = new ArrayList<>();
            depts.forEach(dept -> {
                deptIds.add(dept.getDeptId());
            });
            dto.setDeptIds(deptIds);
            dto.setEnable("false");
            deptIds.forEach(deptId -> {
                if (super.getCurrentUserDeptId().intValue() == deptId.intValue()) {
                    dto.setEnable("true");
                }
            });
        });
        return dtos;
    }

    @Override
    public List<ServicesMapDeptDto> findWithServiceIds(List<Long> serviceIds, String username) {
        return servicesMapDeptRepository.findWithServiceIds(serviceIds, username);
    }

    @Transactional
    @Override
    public void add(ServiceGBTDDto serviceGBTDDto) {
        if (Objects.isNull(serviceGBTDDto.getDeptIds()) || serviceGBTDDto.getDeptIds().isEmpty()) {
            throw new ServerException(ErrorCode.NOT_VALID, "deptIds");
        }
        this.checkExistServiceId(serviceGBTDDto.getServiceId());
        ServiceGBTDEntity entity = super.map(serviceGBTDDto, ServiceGBTDEntity.class);
        entity.setUpdateTime(new Date(System.currentTimeMillis()));
        entity.setUpdateUser(super.getCurrentUsername());

        List<ServicesMapDeptEntity> mapDeptEntities = new ArrayList<>();
        serviceGBTDDto.getDeptIds().forEach(deptId -> {
            // todo them source
            mapDeptEntities.add(new ServicesMapDeptEntity(serviceGBTDDto.getServiceId(), deptId, serviceGBTDDto.getGroupKpiCode()));
        });

        //save to table service_gbtd
//        entity.setTypeParam(null);
        entity = serviceGBTDRepository.save(entity);
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.SERVICES_GBTD, Const.ACTION.INSERT,
                entity.getId(), null, entity));

        //save to table services_map_dept
        mapDeptEntities.forEach(mapDeptEntity -> {
            mapDeptEntity = servicesMapDeptRepository.save(mapDeptEntity);
            logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.SERVICES_MAP_DEPT, Const.ACTION.INSERT,
                    mapDeptEntity.getId(), null, mapDeptEntity));
        });
        UserLogDto userLogDto = new UserLogDto("POST", "CREAT DANH_MUC_CHI_TIEU", MessageUtil.getMessage("code.danh_muc_chi_tieu.create"), objectToJson(serviceGBTDDto));
        userLogService.saveLog(userLogDto);
    }

    @Transactional
    @Override
    public void update(ServiceGBTDDto dto) {
        if (Objects.isNull(dto.getDeptIds()) || dto.getDeptIds().isEmpty()) {
            throw new ServerException(ErrorCode.NOT_VALID, "deptIds");
        }
        this.checkExistServiceId(dto.getServiceId(), dto.getId());

        ServiceGBTDEntity entity = serviceGBTDRepository.findById(dto.getId())
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.SERVICES_GBTD));
        String oldValue = super.toJson(entity);
        super.mapData(dto, entity);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(super.getCurrentUsername());
//        entity.setTypeParam(null);
        entity = serviceGBTDRepository.save(entity);
//        List<ServicesMapDeptEntity> servicesMapDeptEntities = servicesMapDeptRepository.findByServiceIdAndDeptIdIn(dto.getServiceId(), dto.getDeptIds());
//        if (!DataUtil.isNullObject(servicesMapDeptEntities)) {
//            for (ServicesMapDeptEntity en : servicesMapDeptEntities) {
//                en.setGroupKpiCode(dto.getGroupKpiCode());
//                servicesMapDeptRepository.save(en);
//            }
//        }
        //xu ly fix bug update chi tieu
        servicesMapDeptRepository.deleteByServiceIdAndDeptIdIn(dto.getServiceId(), dto.getDeptIds());
        dto.getDeptIds().forEach(e -> servicesMapDeptRepository.save(new ServicesMapDeptEntity(dto.getServiceId(), e, dto.getGroupKpiCode())));

        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.SERVICES_GBTD, Const.ACTION.UPDATE,
                entity.getId(), oldValue, entity));

        List<Long> deptIdsDelete = dto.getDeptIdsDelete();
        if (deptIdsDelete != null && !deptIdsDelete.isEmpty()) {
            deptIdsDelete.forEach(e -> {

                ServicesMapDeptEntity sMap = servicesMapDeptRepository.findByServiceIdAndDeptId(dto.getServiceId(), e);
                List<BiTdServicesTreeEntity> sTrees = biTdServicesTreeRepository.findByParentServiceIdAndParentDeptId(dto.getServiceId(), e);
                List<ServiceGBTDDefineEntity> sDefines = serviceGBTDDefineRepository.findByServiceIdAndDeptId(dto.getServiceId(), e);

                //servicesMapDeptRepository.deleteByServiceIdAndDeptId(dto.getServiceId(), e);
                servicesMapDeptRepository.delete(sMap);
                // delete dinh nghia va cong thuc
                biTdServicesTreeRepository.deleteByParentServiceIdAndParentDeptId(dto.getServiceId(), e);
                serviceGBTDDefineRepository.deleteByServiceIdAndDeptId(dto.getServiceId(), e);

                logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.SERVICES_MAP_DEPT, Const.ACTION.DELETE,
                        sMap.getId(), sMap, null));

                sTrees.forEach(sTree -> {
                    logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.BI_TD_SERVICES_TREE, Const.ACTION.DELETE,
                            sTree.getId(), sTree, null));
                });
                sDefines.forEach(sDefine -> {
                    logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.SERVICE_GBTD_DEFINE, Const.ACTION.DELETE,
                            sDefine.getId(), sDefine, null));
                });
            });
        }

        List<Long> deptIdsAdd = dto.getDeptIdsAdd();
        if (deptIdsAdd != null && !deptIdsAdd.isEmpty()) {
            deptIdsAdd.forEach(e -> {
                ServicesMapDeptEntity servicesMapDeptEntity = new ServicesMapDeptEntity(dto.getServiceId(), e, dto.getGroupKpiCode());
                servicesMapDeptEntity = servicesMapDeptRepository.save(servicesMapDeptEntity);
                logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.SERVICES_MAP_DEPT, Const.ACTION.INSERT,
                        servicesMapDeptEntity.getId(), null, servicesMapDeptEntity));
            });
        }
        UserLogDto userLogDto = new UserLogDto("POST", "UPDATE DANH_MUC_CHI_TIEU", MessageUtil.getMessage("code.danh_muc_chi_tieu.update"), objectToJson(dto));
        userLogService.saveLog(userLogDto);
    }

    @Override
    public void delete(Long serviceId) {
        List<ServiceGBTDEntity> entities = serviceGBTDRepository.findByServiceId(serviceId);
        if (entities.isEmpty()) {
            throw new ServerException(ErrorCode.NOT_FOUND, "serviceId");
        }
        List<ServicesMapDeptEntity> sMaps = servicesMapDeptRepository.findByServiceId(serviceId);
        List<BiTdServicesTreeEntity> sTrees = biTdServicesTreeRepository.findByParentServiceId(serviceId);
        List<ServiceGBTDDefineEntity> sDefines = serviceGBTDDefineRepository.findByServiceId(serviceId);

        serviceGBTDRepository.delete(serviceId);
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.SERVICES_GBTD, Const.ACTION.DELETE,
                entities.get(0).getId(), entities.get(0), null));
        sMaps.forEach(sMap -> {
            logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.SERVICES_MAP_DEPT, Const.ACTION.DELETE,
                    sMap.getId(), sMap, null));
        });

        sTrees.forEach(sTree -> {
            logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.BI_TD_SERVICES_TREE, Const.ACTION.DELETE,
                    sTree.getId(), sTree, null));
        });

        sDefines.forEach(sDefine -> {
            logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.SERVICE_GBTD_DEFINE, Const.ACTION.DELETE,
                    sDefine.getId(), sDefine, null));
        });
        UserLogDto userLogDto = new UserLogDto("POST", "DELETE DANH_MUC_CHI_TIEU", MessageUtil.getMessage("code.danh_muc_chi_tieu.delete"), objectToJson(serviceId));
        userLogService.saveLog(userLogDto);
    }

    @Override
    public ServiceGBTDDto findById(Long id) {
        return serviceGBTDRepository.findById(id)
                .map(e -> super.map(e, ServiceGBTDDto.class))
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.SERVICES_GBTD));
    }

    @Override
    public List<ServiceGBTDDto> findByServiceId(Long serviceId) {
        List<ServiceGBTDEntity> serviceGBTDEntities = serviceGBTDRepository.findByServiceId(serviceId);
        List<ServiceGBTDDto> serviceGBTDDtos = new ArrayList<>();
        for (ServiceGBTDEntity entity : serviceGBTDEntities) {
            Optional<ServiceGBTDEntity> op = Optional.of(entity);
            serviceGBTDDtos.add(op.map(e -> super.map(e, ServiceGBTDDto.class))
                    .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.SERVICES_GBTD)));
        }
        return serviceGBTDDtos;
    }

    private void checkExistServiceId(Long serviceId) {
        List<ServiceGBTDEntity> entities = serviceGBTDRepository.findByServiceIdNative(serviceId);
////        if (Objects.nonNull(serviceGBTDRepository.findByServiceId(serviceId))) {
////            throw new ServerException(ErrorCode.SERVICE_EXIST);
////       }
        if (entities.size() != 0) {
            throw new ServerException(ErrorCode.ALREADY_EXIST, Const.TABLE.SERVICES_GBTD);
        }
    }

    private void checkExistServiceId(Long serviceId, Long id) {
        Optional<ServiceGBTDEntity> opt = serviceGBTDRepository.findById(id);
        if (!opt.isPresent()) {
            throw new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.SERVICES_GBTD);
        }
        ServiceGBTDEntity entity = opt.get();
        if (!entity.getServiceId().equals(serviceId)) {
            //Integer temp = serviceGBTDRepository.countByServiceId(serviceId).intValue();
            List<ServiceGBTDEntity> entities = serviceGBTDRepository.findByServiceIdNative(serviceId);
            if (entities.size() != 0) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, Const.TABLE.SERVICES_GBTD);
            }
        }
    }

    @Override
    public List<BiTdServicesTreeDto> getListServiceFormula(ServiceGBTDDto dto) {
        if (Objects.isNull(dto.getServiceId()) || Objects.isNull(dto.getDeptId())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS);
        }

        List<BiTdServicesTreeDto> dtos = serviceGBTDRepository.getListServiceFormula(dto);
        // Get formula descript.
        ServicesMapDeptEntity entity = servicesMapDeptRepository.findByServiceIdAndDeptId(dto.getServiceId(), dto.getDeptId());
        if (Objects.nonNull(entity)) {
            dtos.forEach(e -> {
                e.setFomularDescript(entity.getFomularDescript());
            });
        }
        return dtos;
    }

    @Override
    public List<ServiceGBTDDto> getAllServiceGbtd() {
        return serviceGBTDRepository.getAllServiceGbtd();
    }

    private void validateServiceFormula(BiTdServicesTreeDto dto) {
        List<BiTdServicesTreeEntity> result = biTdServicesTreeRepository.findByParentServiceIdAndParentDeptIdAndServiceIdAndDeptIdAndTypeParam(
                dto.getParentServiceId(), dto.getParentDeptId(), dto.getServiceId(), dto.getDeptId(), dto.getTypeParam());
        if (Objects.nonNull(result) && !result.isEmpty()) {
            // Truong hop add
            if (Objects.isNull(dto.getId())) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, Const.TABLE.SERVICES_GBTD);
            }

            if (dto.getId().intValue() != result.get(0).getId().intValue()) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, Const.TABLE.SERVICES_GBTD);
            }
        }
    }

    @Override
    @Transactional
    public void saveServiceFormula(List<BiTdServicesTreeDto> dtos) {
//        List<ServicesTreeGBTDEntity> entities = new ArrayList<>();
//        Set<String> keys = new HashSet<>();
//        dtos.forEach(dto -> {
//            this.validateServiceFormula(dto);
//            keys.add(dto.getParentServiceId() + "_" + dto.getServiceId() + "_" + dto.getDeptId());
//
//            ServicesTreeGBTDEntity entity = super.map(dto, ServicesTreeGBTDEntity.class);
//            entity.setUpdateTime(new Date());
//            entity.setUpdateUser(super.getCurrentUsername());
//            entities.add(entity);
//            //entity = servicesTreeGBTDRepository.save(entity);
//
//            ServiceGBTDEntity serviceGBTDEntity = serviceGBTDRepository.findByServiceId(dto.getParentServiceId());
//            if (Objects.isNull(serviceGBTDEntity)) {
//                throw new ServerException(ErrorCode.NOT_FOUND);
//            }
//
//            String formula = serviceGBTDEntity.getFomularDescript() +
//                    " + " +
//                    dto.getServiceName() +
//                    " * " +
//                    dto.getRate();
//            serviceGBTDEntity.setFomularDescript(formula);
//            serviceGBTDEntity = serviceGBTDRepository.save(serviceGBTDEntity);
//        });
//
//        if (dtos.size() == keys.size()) {
//            // Save all dto to Db
//            servicesTreeGBTDRepository.saveAll(entities);
//        } else {
//            throw new ServerException(ErrorCode.SERVICE_EXIST);
//        }
    }

    @Override
    @Transactional
    public void updateServiceFormula(List<BiTdServicesTreeDto> dtos) {
        Map<String, BiTdServicesTreeEntity> entityMap = new HashMap<>();
        Map<String, String> oldValueMap = new HashMap<>();
        if (dtos == null || dtos.isEmpty()) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "service formula");
        }

        Long parentServiceId = dtos.get(0).getParentServiceId();
        Long parentDeptId = dtos.get(0).getParentDeptId();
        //xu ly luu chi tieu cha vao BI_TD_SERVICE_TREE
        List<BiTdServicesTreeDto> serviceParent = dtos.stream().filter(e -> e.isParent() == true)
                .collect(Collectors.toList());
        List<BiTdServicesTreeEntity> list = biTdServicesTreeRepository.findByParentServiceIdAndParentDeptIdAndServiceIdAndDeptIdAndTypeParam(serviceParent.get(0).getParentServiceId(), serviceParent.get(0).getParentDeptId(), serviceParent.get(0).getServiceId(), serviceParent.get(0).getDeptId(), serviceParent.get(0).getTypeParam());
        List<BiTdServicesTreeEntity> result = list.stream().filter(e -> e.getParentDeptId() != null).collect(Collectors.toList());
        //neu don vi cha khac tap doan thi k luu chi tieu cha nua.
        if (list.isEmpty() && checkExistParentServiceId(serviceParent.get(0).getServiceId(), serviceParent.get(0).getDeptCode())) {
            BiTdServicesTreeEntity entity;
            entity = super.map(serviceParent.get(0), BiTdServicesTreeEntity.class);
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(super.getCurrentUsername());
            //set them nhom nganh cho chi tieu cha
            entity.setTypeParam(serviceParent.get(0).getTypeParam());
            entity.setParentDeptId(serviceParent.get(0).getParentDeptId());
            biTdServicesTreeRepository.save(entity);
            //ghi log cho chi tieu cha
            logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.BI_TD_SERVICES_TREE, Const.ACTION.INSERT,
                    entity.getId(), null, entity));

        }

        //luu chi tieu con vao BI_TD_SERVICE_TREE
        List<BiTdServicesTreeDto> serviceChildren = dtos.stream().filter(e -> e.isParent() == false)
                .collect(Collectors.toList());
        serviceChildren.forEach(dto -> {
            this.validateServiceFormula(dto);
            BiTdServicesTreeEntity entity;
            String oldValue = null;
            if (Objects.nonNull(dto.getId())) {
                entity = biTdServicesTreeRepository.findById(dto.getId())
                        .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "serviceTree"));
                oldValue = super.toJson(entity);
                super.mapData(dto, entity);
            } else {
                entity = super.map(dto, BiTdServicesTreeEntity.class);
            }
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(super.getCurrentUsername());
            //set them nhom nganh cho chi tieu
            entity.setTypeParam(dto.getTypeParam());

            String key = dto.getParentServiceId() + "_" + dto.getServiceId() + "_" + dto.getDeptId();
            if (entityMap.containsKey(key)) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, Const.TABLE.SERVICES_GBTD);
            }

            entityMap.put(key, entity);
            oldValueMap.put(key, oldValue);

            //update source to services_map_dept
//            this.updateSourceForParentService(dto.getServiceId(), dto.getDeptId(), dto.getSource());
        });

        biTdServicesTreeRepository.saveAll(entityMap.values());
        for (Map.Entry<String, BiTdServicesTreeEntity> e : entityMap.entrySet()) {
            String action = StringUtils.isEmpty(oldValueMap.get(e.getKey())) ? Const.ACTION.INSERT : Const.ACTION.UPDATE;
            logActionService.saveLogActionInternal(super.createLogDto(
                    Const.TABLE.BI_TD_SERVICES_TREE, action, e.getValue().getId(), oldValueMap.get(e.getKey()), e.getValue()));
        }

        // update formula
        this.updateFormulaForParentService(parentServiceId, parentDeptId);
        UserLogDto userLogDto = new UserLogDto("POST", "UPDATE CONG_THUC_CHI_TIEU", MessageUtil.getMessage("code.cong_thuc_chi_tieu.update"), objectToJson(dtos));
        userLogService.saveLog(userLogDto);

    }

    @Override
    @Transactional
    public void deleteServiceFormula(BiTdServicesTreeDto dto) {
        if (Objects.isNull(dto.getId()))
            throw new ServerException(ErrorCode.NOT_FOUND);
        Optional<BiTdServicesTreeEntity> opt = biTdServicesTreeRepository.findById(dto.getId());
        if (!opt.isPresent()) {
            throw new ServerException(ErrorCode.NOT_FOUND);
        }

        biTdServicesTreeRepository.deleteById(dto.getId());
        logActionService.saveLogActionInternal(super.createLogDto(Const.TABLE.BI_TD_SERVICES_TREE, Const.ACTION.DELETE,
                opt.get().getId(), opt.get(), null));

        this.updateFormulaForParentService(opt.get().getParentServiceId(), opt.get().getParentDeptId());
        UserLogDto userLogDto = new UserLogDto("POST", "DELETE CONG_THUC_CHI_TIEU", MessageUtil.getMessage("code.cong_thuc_chi_tieu.delete"), objectToJson(dto.getId()));
        userLogService.saveLog(userLogDto);
    }

    @Override
    public List<ServiceGBTDDto> findServicesByDeptId(Long deptId, Long parentServiceId) {
        if (Objects.isNull(deptId) || Objects.isNull(parentServiceId)) {
            throw new ServerException(ErrorCode.MISSING_PARAMS);
        }
        List<ServiceGBTDDto> dtos = new ArrayList<>();
        // Find serviceIds in table services_map_dept
        List<ServicesMapDeptEntity> serviceIds = servicesMapDeptRepository.findByDeptId(deptId);

        // foreach serviceId -> find ServiceGBTD
        serviceIds.forEach(serviceId -> {
            List<ServiceGBTDEntity> entities = serviceGBTDRepository.findByServiceIdAndStatus(serviceId.getServiceId(), Const.STATUS.ACTIVE);
            if (!entities.isEmpty()) {
                dtos.add(super.map(entities.get(0), ServiceGBTDDto.class));
            }
        });
        return dtos;
    }

    private void updateFormulaForParentService(Long parentServiceId, Long parentDeptId) {
        ActionAuditDto.Builder logBuilder = super.defaultLogBuilder()
                .action(Const.ACTION.UPDATE)
                .tableName(Const.TABLE.SERVICES_MAP_DEPT);
        Map<String, String> formulaMap = new HashMap<>();
        List<BiTdServicesTreeDto> listServiceFormula = serviceGBTDRepository.getListServiceFormula(new ServiceGBTDDto(parentServiceId, parentDeptId));
        listServiceFormula.forEach(e -> {
            String formula = formulaMap.get(e.getDeptCode());
            formulaMap.put(e.getDeptCode(), formula == null
                    ? e.getServiceName() + " * " + e.getRate()
                    : formula + " + " + e.getServiceName() + " * " + e.getRate());
        });
        StringJoiner formula = new StringJoiner(" + ");
        formulaMap.forEach((deptCode, deptFormula) -> formula.add(deptCode + "(" + deptFormula + ")"));

        ServicesMapDeptEntity smdEntity = servicesMapDeptRepository.findByServiceIdAndDeptId(parentServiceId, parentDeptId);
        logBuilder.oldValue(smdEntity);

        smdEntity.setFomularDescript(formula.toString());
        smdEntity = servicesMapDeptRepository.save(smdEntity);
        logBuilder.newValue(smdEntity);
        logBuilder.objectId(smdEntity.getId());

        super.saveLog(logBuilder.build());

//        int result = serviceGBTDRepository.updateFormulaServiceParent(parentServiceId, parentDeptId, formula.toString());
//        if (result == 0) {
//            throw new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.SERVICES_GBTD);
//        }
    }

    @Override
    public List<ServiceGBTDDto> findByIds(List<Long> serviceIds) {
        return super.mapList(serviceGBTDRepository.findByServiceIdIn(serviceIds), ServiceGBTDDto.class);
    }

    @Override
    public Optional<ServiceGBTDChartDto> findByKpiIdWithRate(Long kpiId, Long unitIdView) {
        log.debug("Request to get serviceGbtd with serviceId : {}", kpiId);
        return serviceGBTDRepository.findFirstByServiceIdAndStatus(kpiId, Const.STATUS.ACTIVE)
                .map(e -> {
                    ServiceGBTDChartDto dto = super.map(e, ServiceGBTDChartDto.class);
                    // bind unit
                    List<CatUnitDto> units = catUnitService.findByIds(Arrays.asList(dto.getUnitId(), unitIdView));

                    CatUnitDto unitService = units.stream()
                            .filter(unit -> unit.getId().equals(dto.getUnitId()))
                            .findFirst()
                            .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "unit of service"));
                    dto.setUnitName(unitService.getName());

                    if (null != unitIdView) {
                        CatUnitDto unitView = units.stream()
                                .filter(unit -> unit.getId().equals(unitIdView))
                                .findFirst()
                                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "unit of chart"));
                        dto.setUnitViewId(unitView.getId());
                        dto.setUnitViewName(unitView.getName());
                        dto.setUnitViewCode(unitView.getCode());
                        dto.setUnitDisplay(unitView.getNameDisplay());

                        List<CatUnitRateDto> converter = catUnitService.findConverter(dto.getUnitId(), unitIdView);
                        if (!DataUtil.isNullOrEmpty(converter)) {
                            if (converter.get(0).getUnitIdAfter().equals(unitIdView)) {
                                dto.setRate(1D / converter.get(0).getRate());
                            } else {
                                dto.setRate((double) converter.get(0).getRate());
                            }
                        }
                    } else {
                        dto.setRate(1D);
                        dto.setUnitDisplay(unitService.getNameDisplay());
                    }

                    // get map dept
                    List<ServicesMapDeptDto> mapDeptDtos = super.mapList(servicesMapDeptRepository.findByServiceId(
                            dto.getServiceId()),
                            ServicesMapDeptDto.class
                    );
                    dto.setKpiMapDept(mapDeptDtos);

                    return Optional.of(dto);
                })
                .orElseGet(Optional::empty);
    }

    @Override
    public String haveFormulaOrDefine(ServiceGBTDDto dto) {
        if (dto.getServiceId() == null) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "serviceId");
        }
        if (dto.getDeptIdsDelete() == null || dto.getDeptIdsDelete().isEmpty()) {
            return "false";
        }
        for (Long deptId : dto.getDeptIdsDelete()) {
            List<ServiceGBTDDefineEntity> entities = serviceGBTDDefineRepository.findByServiceIdAndDeptId(dto.getServiceId(), deptId);
            if (entities.size() != 0) {
                return "true";
            }

            List<BiTdServicesTreeEntity> biEntities = biTdServicesTreeRepository
                    .findByParentServiceIdAndParentDeptId(dto.getServiceId(), deptId);
            if (biEntities.size() != 0) {
                return "true";
            }
        }
        return "false";
    }

    @Override
    public List<ServiceGBTDDto> findServiceOfDept(ServiceGBTDDto dto) {
        if (DataUtil.isNullOrEmpty(dto.getIds())) {
            if (DataUtil.isNullOrEmpty(dto.getDeptIds())) {
                throw new ServerException(ErrorCode.MISSING_PARAMS, "ids");
            }
        }

        if (null == dto.getTypeParams()) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "typeParam");
        }
        return serviceGBTDRepository.findServiceOfDept(dto);
    }

    @Override
    public List<ServiceGBTDDto> findChildrenService(ServiceGBTDDto dto) {
        if (null == dto.getDeptId() || null == dto.getServiceId()) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "parentDeptId, parentServiceId");
        }
        return serviceGBTDRepository.findChildrenService(dto);
    }

    public boolean checkExistParentServiceId(Long serviceId, String deptCode) {
        //kiem tra xem chi tieu nay co laf chi teu con cua thang nao k
        //với chỉ tiêu này check xem parent deptcode và parentDeptId của nó có khác null k
        List<BiTdServicesTreeEntity> biTdServicesTreeEntityList = biTdServicesTreeRepository.findByDeptCodeAndServiceIdAndParentDeptCodeNotNull(deptCode, serviceId);
        if (biTdServicesTreeEntityList.isEmpty()) {
            return true;
        } else
            return false;
    }
}
