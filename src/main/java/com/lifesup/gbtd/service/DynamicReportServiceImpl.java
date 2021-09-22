package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.TempReportEntity;
import com.lifesup.gbtd.repository.CatItemRepository;
import com.lifesup.gbtd.repository.ParamTreeRepository;
import com.lifesup.gbtd.repository.ServiceGBTDRepository;
import com.lifesup.gbtd.repository.TempReportRepository;
import com.lifesup.gbtd.repository.UsersRepository;
import com.lifesup.gbtd.service.inteface.ICatDepartmentService;
import com.lifesup.gbtd.service.inteface.IDynamicReportService;
import com.lifesup.gbtd.service.inteface.ILogActionService;
import com.lifesup.gbtd.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class DynamicReportServiceImpl extends BaseService implements IDynamicReportService {

    private final CatItemRepository catItemRepository;
    private final ICatDepartmentService catDepartmentService;
    private final ParamTreeRepository paramTreeRepository;
    private final ServiceGBTDRepository serviceGBTDRepository;
    private final UsersRepository usersRepository;
    private final TempReportRepository tempReportRepository;
    private final ILogActionService logActionService;
    private final UserLogService userLogService;

    @Autowired
    public DynamicReportServiceImpl(CatItemRepository catItemRepository,
                                    ICatDepartmentService catDepartmentService,
                                    ParamTreeRepository paramTreeRepository,
                                    ServiceGBTDRepository serviceGBTDRepository,
                                    UsersRepository usersRepository,
                                    TempReportRepository tempReportRepository,
                                    ILogActionService logActionService, UserLogService userLogService) {
        this.catItemRepository = catItemRepository;
        this.catDepartmentService = catDepartmentService;
        this.paramTreeRepository = paramTreeRepository;
        this.serviceGBTDRepository = serviceGBTDRepository;
        this.usersRepository = usersRepository;
        this.tempReportRepository = tempReportRepository;
        this.logActionService = logActionService;
        this.userLogService = userLogService;
    }

    @Override
    public HashMap<String, List> getCatItems() {
        HashMap<String, List> data = new HashMap<>();
        data.put("itemName", catItemRepository.findItemNameByCategoryCode(Const.CAT_ITEM_CODE.SHEET_BIRT));
//        data.put("catDepartment", catDepartmentRepository.getDepartmentsByName(super.getCurrentUsername()));
        return data;
    }

    @Override
    public TempReportDto add(TempReportDto tempReportDTO) {
        String userName = super.getCurrentUsername();
        tempReportDTO.setReportName(tempReportDTO.getReportName().trim().replaceAll("\\s+", " "));
        List<TempReportEntity> list = tempReportRepository.findByReportNameIgnoreCaseAndOwnerBy(tempReportDTO.getReportName(), userName);
        if (list.size() > 0) {
            throw new ServerException(ErrorCode.ALREADY_EXIST, "report name");
        }
        tempReportDTO.setStatus(Const.STATUS.ACTIVE);
        tempReportDTO.setOwnerBy(userName);
        tempReportDTO.setCreateUser(userName);
        TempReportEntity entity = tempReportRepository.save(super.map(tempReportDTO, TempReportEntity.class));
        tempReportDTO.setReportId(entity.getReportId());

        logActionService.saveLogActionInternal(
                super.createLogDto(Const.TABLE.TEMP_REPORT, Const.ACTION.INSERT, entity.getReportId(), null, entity));
        //Thai_ ghi log khi add bao cao dong
//        UserLogDto userLogDto = new UserLogDto("POST","SAVE BAO_CAO_DONG","Luu file bao cao dong",objectToJson(tempReportDTO));
//        userLogService.saveLog(userLogDto);
        return tempReportDTO;
    }

    @Override
    public void update(TempReportDto tempReportDTO) {
        String userName = super.getCurrentUsername();
        TempReportEntity old = tempReportRepository.findById(tempReportDTO.getReportId())
                .orElseThrow(() -> new ServerException("Report id not found"));
        String oldValue = super.toJson(old);
        tempReportDTO.setReportName(tempReportDTO.getReportName().trim().replaceAll("\\s+", " "));
        List<TempReportEntity> list = tempReportRepository.findByReportNameIgnoreCaseAndOwnerBy(tempReportDTO.getReportName(), userName);
        if (list.size() > 0 && !Objects.equals(list.get(0).getReportId(), tempReportDTO.getReportId())) {
            throw new ServerException(ErrorCode.ALREADY_EXIST, "report name");
        }
        tempReportDTO.setStatus(Const.STATUS.ACTIVE);
        tempReportDTO.setOwnerBy(userName);
        tempReportDTO.setCreateUser(userName);
        TempReportEntity entity = tempReportRepository.save(super.map(tempReportDTO, TempReportEntity.class));
        logActionService.saveLogActionInternal(
                super.createLogDto(Const.TABLE.TEMP_REPORT, Const.ACTION.UPDATE, tempReportDTO.getReportId(), oldValue, entity));
    }

    @Override
    public void delete(TempReportDto tempReportDTO) {
        TempReportEntity tempReport = tempReportRepository.findById(tempReportDTO.getReportId())
                .map(te -> {
                    paramTreeRepository.deleteTempReport(tempReportDTO);
                    return te;
                })
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "tempReport"));
        logActionService.saveLogActionInternal(
                super.createLogDto(Const.TABLE.TEMP_REPORT, Const.ACTION.DELETE, tempReportDTO.getReportId(), tempReport, null));
    }

    @Override
    public void shareReport(TempReportDto tempReportDTO) {
        String userName = super.getCurrentUsername();
        tempReportDTO.setStatus(Const.STATUS.SHARED);
        tempReportDTO.setCreateUser(userName);
        tempReportDTO.setReportId(null);
        TempReportEntity entity = tempReportRepository.save(super.map(tempReportDTO, TempReportEntity.class));
        logActionService.saveLogActionInternal(
                super.createLogDto(Const.TABLE.TEMP_REPORT, Const.ACTION.INSERT, entity.getReportId(), null, entity));
        //Thai_ ghi log khi share bao cao dong
        UserLogDto userLogDto = new UserLogDto("POST","SHARE BAO_CAO_DONG","share file bao cao dong",objectToJson(tempReportDTO));
        userLogService.saveLog(userLogDto);
    }

    @Override
    public List<TempReportDto> getListTempReport() {
        String userName = super.getCurrentUsername();
        return paramTreeRepository.getListTempReport(userName);
    }

    @Override
    public List<ServiceGBTDDto> getListTargetService(ServiceGBTDDto dto) {
        if (Objects.nonNull(dto.getServiceId())) {
            return serviceGBTDRepository.findChildrenService(dto);
        } else if (Objects.nonNull(dto.getDeptIds())) {
            return serviceGBTDRepository.findServiceGBTDByDeptIdIn(dto);
        } else if (Objects.nonNull(dto.getDeptId())) {
            return serviceGBTDRepository.findServiceGBTDByDeptId(dto);
        }
        throw new ServerException(ErrorCode.MISSING_PARAMS);
    }

    @Override
    public List<UsersDto> getListUsers() {
        return super.mapList(usersRepository.findUsersByDeptIdInAndIdNot(super.getCurrentUserDeptId(), super.getCurrentUserId()), UsersDto.class);
    }

    @Override
    public TempReportDto getTempReport(TempReportDto dto) {
        return tempReportRepository.findById(dto.getReportId())
                .map(tr -> super.map(tr, TempReportDto.class))
                .orElseThrow(() -> new ServerException("Report id not found"));
    }

    @Override
    public List<CatDepartmentDto> getDepartmentTreeFromDeptId(CatDepartmentDto criteria) {
        if (Objects.isNull(criteria.getDeptId())) {
            log.info("deptId null, get dept id from user: " + super.getCurrentUserDeptId());
            criteria.setDeptId(super.getCurrentUserDeptId());
        }
        return catDepartmentService.getDeptTreeByDeptId2(criteria.getDeptId(), Arrays.asList(
                Const.DEPT_LEVEL.TAP_DOAN,
                Const.DEPT_LEVEL.TCT_CTY_PHB,
                Const.DEPT_LEVEL.THI_TRUONG
        ), criteria.getTypeParam());
    }
}
