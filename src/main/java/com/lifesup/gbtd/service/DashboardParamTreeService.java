package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.DashboardParamTreeEntity;
import com.lifesup.gbtd.repository.CatDepartmentRepository;
import com.lifesup.gbtd.repository.DashboardParamTreeRepository;
import com.lifesup.gbtd.service.inteface.IDashboardParamTreeService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class DashboardParamTreeService extends BaseService implements IDashboardParamTreeService {
    private final UserLogService userLogService;
    private final DashboardParamTreeRepository dashboardParamTreeRepository;
    private final CatDepartmentRepository catDepartmentRepository;
    public static final Long PARAM_ORDER_DEFAULT = 1L;

    @Autowired
    public DashboardParamTreeService(UserLogService userLogService, DashboardParamTreeRepository dashboardParamTreeRepository,
                                     CatDepartmentRepository catDepartmentRepository) {
        this.userLogService = userLogService;
        this.dashboardParamTreeRepository = dashboardParamTreeRepository;
        this.catDepartmentRepository = catDepartmentRepository;
    }

    @Override
    public List<DashboardParamTreeDto> update(SaveDashboardParamTreeDto dto) {
        List<ActionAuditDto> actionLogs = new ArrayList<>();
        List<DashboardParamTreeDto> doReturns = new ArrayList<>();
        this.validateSave(dto.getDashboardParamTreeDtos());
        String typeParam = dto.getDashboardParamTreeDtos().get(0).getTypeParam();
        this.checkParentParamExist(typeParam, dto);
        dto.getDashboardParamTreeDtos().forEach(dptDto -> {
            if (null != dptDto.getId()) {
                dptDto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.UPDATE));
            } else {
                dptDto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.INSERT));
            }
            dptDto.setModifiedDate(new Date());
            ActionAuditDto.Builder logDashBoard = dptDto.getLogBuilder()
                    .tableName(Const.DASHBOARD_PARAM_TREE)
                    .oldValue(null != dptDto.getId()
                            ? dashboardParamTreeRepository.findById(dptDto.getId()).orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "dashboardParamTree"))
                            : null);
            DashboardParamTreeEntity entity = dashboardParamTreeRepository.save(super.map(dptDto, DashboardParamTreeEntity.class));
            actionLogs.add(logDashBoard
                    .newValue(entity)
                    .objectId(entity.getId())
                    .build());
            doReturns.add(super.map(entity, DashboardParamTreeDto.class));
        });
        super.saveLog(actionLogs);
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "UPDATE NHOM_DON_VI", MessageUtil.getMessage("code.nhom_don_vi.update"), objectToJson(dto));
        userLogService.saveLog(userLogDto);
        return doReturns;
    }

    @Override
    public void delete(DashboardParamTreeDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new ServerException(ErrorCode.NOT_FOUND, "id");
        }
        DashboardParamTreeEntity entity = dashboardParamTreeRepository.findById(dto.getId())
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "DashboardPramTree"));
        super.saveLog(super.deleteLog(Const.DASHBOARD_PARAM_TREE, dto.getId(), entity));
        dashboardParamTreeRepository.delete(entity);
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "DELETE NHOM_DON_VI", MessageUtil.getMessage("code.nhom_don_vi.delete"), objectToJson(dto));
        userLogService.saveLog(userLogDto);
    }

    @Override
    public List<DashboardParamTreeDto> dashboardParamTreeByParentDeptId(DashboardParamTreeDto dto) {
        return super.mapList(
                dashboardParamTreeRepository.findByTypeParamAndParentDeptId(dto.getTypeParam(), dto.getParentDeptId()),
                DashboardParamTreeDto.class);
    }

    @Override
    public List<CatDepartmentDto> doSearch(DashboardParamTreeDto dto) {
        if (StringUtils.isEmpty(dto.getTypeParam())) {
            dto.setTypeParam("DTTD");
        }
        List<CatDepartmentDto> dtoList = dashboardParamTreeRepository.doSearch(dto);
        //ghi log
//        UserLogDto userLogDto = new UserLogDto("GET", "SEARCH NHOM_DON_VI", "Tìm kiếm nhóm đơn vị");
//        userLogService.saveLog(userLogDto);
        return dtoList;
    }

    private void validateSave(List<DashboardParamTreeDto> dtos) {
        if (DataUtil.isNullOrEmpty(dtos)) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "dashboardParamTree");
        }
        List<String> checkContains = new ArrayList<>();
        dtos.forEach(dpt -> {
            dashboardParamTreeRepository
                    .findByTypeParamAndParentDeptIdAndDeptId(dpt.getTypeParam(), dpt.getParentDeptId(), dpt.getDeptId())
                    .stream().findFirst()
                    .filter(dr -> Objects.isNull(dpt.getId()) || !dpt.getId().equals(dr.getId()))
                    .ifPresent(entity -> {
                        throw new ServerException(ErrorCode.ALREADY_EXIST, "DashboardPramTree");
                    });
            catDepartmentRepository.findById(dpt.getParentDeptId())
                    .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "catDepartment"));
            String value = dpt.getParentDeptId() + "_" + dpt.getDeptId() + "_" + dpt.getTypeParam();
            if (checkContains.contains(value)) {
                throw new ServerException(ErrorCode.ALREADY_EXIST, "DashboardPramTree");
            }
            checkContains.add(value);
        });
    }

    private void checkParentParamExist(String typeParam, SaveDashboardParamTreeDto dto) {
        DashboardParamTreeEntity parentEntity = dashboardParamTreeRepository.findByTypeParamAndDeptIdAndStatus(
                typeParam,
                dto.getCatDepartmentDto().getId(),
                Const.STATUS.ACTIVE)
                .stream()
                .findFirst()
                .orElse(null);
        if (null == parentEntity) {
            // no parent, create new
            dto.getDashboardParamTreeDtos().add(new DashboardParamTreeDto(
                    dto.getCatDepartmentDto().getCode(),
                    dto.getCatDepartmentDto().getName(),
                    dto.getCatDepartmentDto().getParentCode(),
                    typeParam,
                    Const.STATUS.ACTIVE,
                    PARAM_ORDER_DEFAULT,
                    dto.getCatDepartmentDto().getStartTime(),
                    dto.getCatDepartmentDto().getEndTime(),
                    dto.getCatDepartmentDto().getId(),
                    dto.getCatDepartmentDto().getParentId(),
                    typeParam)
            );
        }
    }

    @Override
    public List<CatDepartmentDto> getListDashboardParam() {
        return dashboardParamTreeRepository.doSearchList();
    }

}
