package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.ActionAuditDto;
import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.ParamTreeDto;
import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.CatDepartmentEntity;
import com.lifesup.gbtd.repository.CatDepartmentRepository;
import com.lifesup.gbtd.repository.CatLocationRepository;
import com.lifesup.gbtd.repository.DashboardParamTreeRepository;
import com.lifesup.gbtd.service.inteface.ICatDepartmentService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CatDepartmentService extends BaseService implements ICatDepartmentService {

    private static final String CAT_DEPARTMENT = "Cat Department";

    @Autowired
    public CatDepartmentService(CatDepartmentRepository catDepartmentRepository, CatLocationRepository catLocationRepository,
                                DashboardParamTreeRepository dashboardParamTreeRepository, UserLogService userLogService) {
        this.catDepartmentRepository = catDepartmentRepository;
        this.catLocationRepository = catLocationRepository;
        this.dashboardParamTreeRepository = dashboardParamTreeRepository;
        this.userLogService = userLogService;
    }

    CatDepartmentRepository catDepartmentRepository;
    CatLocationRepository catLocationRepository;
    DashboardParamTreeRepository dashboardParamTreeRepository;
    private final UserLogService userLogService;


    @Override
    public List<CatDepartmentDto> getDepartmentsCurrentUser(List<Long> deptLevels) {
        return this.getDeptTreeByDeptId(super.getCurrentUserDeptId(), deptLevels);
    }

    @Override
    public List<CatDepartmentDto> getDeptTreeByDeptId2(Long deptId, List<Long> deptLevels,String typeParam) {
        return catDepartmentRepository.getDepartmentTreeByDeptId2(deptId, deptLevels,typeParam);
    }
    @Override
    public List<CatDepartmentDto> getDeptTreeByDeptId(Long deptId, List<Long> deptLevels) {
        return catDepartmentRepository.getDepartmentTreeByDeptId(deptId, deptLevels);
    }

    @Override
    public List<CatDepartmentDto> getDepartmentByDeptLevel(Long[] inputLevel, String fullTree) {
        if (null == inputLevel) {
            inputLevel = new Long[]{
                    Const.DEPT_LEVEL.TAP_DOAN,
                    Const.DEPT_LEVEL.TCT_CTY_PHB,
                    Const.DEPT_LEVEL.TINH_TP,
                    Const.DEPT_LEVEL.QUAN_HUYEN,
                    Const.DEPT_LEVEL.THI_TRUONG
            };
        }
        if ("YES".equalsIgnoreCase(fullTree)) {
            return this.getDeptTreeByDeptId(null, Arrays.asList(inputLevel));
        } else {
            return this.getDeptTreeByDeptId(Const.ID_TAP_DOAN, Arrays.asList(inputLevel));
        }
    }

    @Override
    public List<CatDepartmentDto> getDepartmentsForDVCT() {
        List<Long> deptLevels;
        Long lvCurrentUser = super.getCurrentUserDeptLevel();

        if (lvCurrentUser.equals(Const.DEPT_LEVEL.TAP_DOAN)) {
            deptLevels = Arrays.asList(Const.DEPT_LEVEL.TAP_DOAN, Const.DEPT_LEVEL.TCT_CTY_PHB, Const.DEPT_LEVEL.THI_TRUONG);
        } else if (lvCurrentUser.equals(Const.DEPT_LEVEL.THI_TRUONG)) {
            deptLevels = Collections.singletonList(Const.DEPT_LEVEL.THI_TRUONG);
        } else {
            deptLevels = Arrays.asList(Const.DEPT_LEVEL.TCT_CTY_PHB, Const.DEPT_LEVEL.THI_TRUONG);
        }
        return this.getDeptTreeByDeptId(super.getCurrentUserDeptId(), deptLevels);
    }

    @Override
    public List<CatDepartmentDto> getParamTreeDept(ParamTreeDto obj) {
        if (null == obj.getTypeParams()) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "typeParam");
        }
        obj.setDeptId(super.getCurrentUserDeptId());
        return catDepartmentRepository.getParamTreeDept(obj);
    }

    @Override
    @Transactional
    public List<CatDepartmentDto> doSearch(CatDepartmentDto departmentDto) {
        //ghi log
//        UserLogDto userLogDto = new UserLogDto("GET", "SEARCH QUAN_LY_NHOM_DON_VI", "Tìm kiếm quản lý nhóm đơn vị");
//        userLogService.saveLog(userLogDto);
        return catDepartmentRepository.getDepartmentTreeByDeptLevelAndName(departmentDto);
    }

    @Override
    @Transactional
    public CatDepartmentDto createCatDepartment(CatDepartmentDto dto) {
        if (null != dto.getId()) {
            throw new ServerException(ErrorCode.ALREADY_EXIST, CAT_DEPARTMENT);
        }
        dto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.INSERT));
        this.validateSave(dto);
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "CREATE QUAN_LY_NHOM_DON_VI", MessageUtil.getMessage("code.quan_ly_nhom_don_vi.create"), objectToJson(dto));
        userLogService.saveLog(userLogDto);
        return this.save(dto);
    }

    @Override
    public CatDepartmentDto updateCatDepartment(CatDepartmentDto dto) {
        if (null == dto.getId()) {
            throw new ServerException(ErrorCode.NOT_FOUND, CAT_DEPARTMENT);
        }
        dto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.UPDATE));
        this.validateSave(dto);
        CatDepartmentDto catDepartmentDto = this.save(dto);
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "UPDATE QUAN_LY_NHOM_DON_VI", MessageUtil.getMessage("code.quan_ly_nhom_don_vi.update"), objectToJson(dto));
        userLogService.saveLog(userLogDto);
        this.updateDashboardParamTree(catDepartmentDto);
        return catDepartmentDto;
    }

    @Override
    public void delete(CatDepartmentDto dto) {
        List<ActionAuditDto> actionLogs = new ArrayList<>();
        if (null == dto.getId()) {
            throw new ServerException(ErrorCode.NOT_FOUND, "Id");
        }
        dashboardParamTreeRepository.findByDeptIdOrParentDeptId(dto.getId(), dto.getId())
                .forEach(dpt -> {
                    actionLogs.add(super.deleteLog(Const.DASHBOARD_PARAM_TREE, dpt.getId(), dpt));
                    dashboardParamTreeRepository.delete(dpt);
                });
        CatDepartmentEntity catDepartment = catDepartmentRepository.findById(dto.getId())
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, CAT_DEPARTMENT));
        actionLogs.add(super.deleteLog(Const.TABLE.CAT_DEPARTMENT, dto.getId(), catDepartment));
        catDepartmentRepository.delete(catDepartment);
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "DELETE QUAN_LY_NHOM_DON_VI", MessageUtil.getMessage("code.quan_ly_nhom_don_vi.delete"), objectToJson(dto));
        userLogService.saveLog(userLogDto);
        super.saveLog(actionLogs);
    }

    @Override
    public CatDepartmentDto findById(Long id) {
        return catDepartmentRepository.findByIdAndStatus(id, Const.STATUS.ACTIVE)
                .map(c -> super.map(c, CatDepartmentDto.class))
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, CAT_DEPARTMENT));
    }

    private void validateSave(CatDepartmentDto dto) {
        catDepartmentRepository.findByCode(dto.getCode()).stream()
                .findFirst()
                .filter(ce -> !ce.getId().equals(dto.getId()))
                .ifPresent(cer -> {
                    throw new ServerException(ErrorCode.ALREADY_EXIST, CAT_DEPARTMENT);
                });
    }

    private CatDepartmentDto save(CatDepartmentDto dto) {
        dto.setUpdateTime(new Date());
        dto.setUpdateUser(super.getCurrentUsername());
        if (null != dto.getLocationDto()) {
            dto.setCountryId(dto.getLocationDto().getCountryId());
            dto.setCountryCode(dto.getLocationDto().getCountryCode());
            dto.setCountryName(dto.getLocationDto().getCountryName());
        }
        ActionAuditDto.Builder logDashBoard = dto.getLogBuilder()
                .tableName(Const.TABLE.CAT_DEPARTMENT)
                .oldValue(null != dto.getId()
                        ? catDepartmentRepository.findById(dto.getId()).orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, CAT_DEPARTMENT))
                        : null);

        if (null != dto.getParentDepartmentDto()) {
            CatDepartmentDto parent = dto.getParentDepartmentDto();
            dto.setDeptLevelCodeFull(parent.getDeptLevelCodeFull() + dto.getCode() + "/");
            dto.setDeptLevelNameFull(parent.getDeptLevelNameFull() + dto.getName() + "/");
            dto.setParentId(parent.getId());
            dto.setParentCode(parent.getCode());
//            if (Arrays.asList(Const.DEPT_LEVEL.THI_TRUONG,
//                    Const.DEPT_LEVEL.TINH_TP,
//                    Const.DEPT_LEVEL.QUAN_HUYEN)
//                    .contains(parent.getDeptLevel())) {
                if (null != dto.getLocationDto()) {
                    dto.setProvinceId(dto.getLocationDto().getProvinceId());
                    dto.setProvinceCode(dto.getLocationDto().getProvinceCode());
                    dto.setProvinceName(dto.getLocationDto().getProvinceName());
                }

            try {
                List<String> parentTreeCodes = Arrays.asList(parent.getDeptLevelCodeFull().split("/"));
                CatDepartmentEntity company = catDepartmentRepository.findByCodeInAndStatus(
                        parentTreeCodes, Const.STATUS.ACTIVE)
                        .stream().filter(d -> d.getDeptLevel()
                                .equals((dto.getDeptLevel().equals(Const.DEPT_LEVEL.TINH_TP) || dto.getDeptLevel().equals(Const.DEPT_LEVEL.QUAN_HUYEN)) ?
                                        Const.DEPT_LEVEL.TCT_CTY_PHB :
                                        Const.DEPT_LEVEL.NO_DEPT_LEVEL))
                        .findAny()
                        .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "company"));

                dto.setCompanyId(company.getId());
                dto.setCompanyCode(company.getCode());
                dto.setCompanyName(company.getName());
            } catch (ServerException e) {
                dto.setCompanyId(null);
                dto.setCompanyCode(null);
                dto.setCompanyName(null);
                log.error("loi khong tim thay company", e);
            }
//            }
        } else {
            dto.setDeptLevelCodeFull("/" + dto.getCode() + "/");
            dto.setDeptLevelNameFull("/" + dto.getName() + "/");
        }

        CatDepartmentEntity entity = catDepartmentRepository.save(super.map(dto, CatDepartmentEntity.class));
        super.saveLog(logDashBoard
                .newValue(entity)
                .objectId(entity.getId())
                .build());
        return super.map(entity, CatDepartmentDto.class);
    }

    private void updateDashboardParamTree(CatDepartmentDto dto) {
        List<ActionAuditDto> actionLogs = new ArrayList<>();
        dashboardParamTreeRepository.findByDeptIdAndStatus(dto.getId(), Const.STATUS.ACTIVE)
                .forEach(dpt -> {
                    String oldValue = super.toJson(dpt);
                    dpt.setCode(dto.getCode());
                    dpt.setName(dto.getName());
                    dpt.setModifiedDate(new Date());
                    dpt.setStartTime(dto.getStartTime());
                    dpt.setEndTime(dto.getEndTime());
                    dashboardParamTreeRepository.save(dpt);
                    actionLogs.add(super.updateLog(Const.DASHBOARD_PARAM_TREE, dpt.getId(), oldValue, dpt));
                });
        dashboardParamTreeRepository.findByParentDeptId(dto.getId())
                .forEach(dpt -> {
                    String oldValue = super.toJson(dpt);
                    dpt.setParent(dto.getCode());
                    dpt.setModifiedDate(new Date());
                    dashboardParamTreeRepository.save(dpt);
                    actionLogs.add(super.updateLog(Const.DASHBOARD_PARAM_TREE, dpt.getId(), oldValue, dpt));
                });
        super.saveLog(actionLogs);
    }
}
