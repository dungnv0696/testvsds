package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.DashboardFolderEntity;
import com.lifesup.gbtd.repository.DashboardFolderRepository;
import com.lifesup.gbtd.repository.DashboardReportRepository;
import com.lifesup.gbtd.service.inteface.IDashboardFolderService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.birt.report.data.adapter.i18n.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DashboardFolderService extends BaseService implements IDashboardFolderService {

    private final DashboardFolderRepository dashboardFolderRepository;
    private final DashboardReportRepository dashboardReportRepository;
    private final UserLogService userLogService;
    private static final String DASHBOARD_FOLDER = "Dashboard Folder";

    @Autowired
    public DashboardFolderService(DashboardFolderRepository dashboardFolderRepository, DashboardReportRepository dashboardReportRepository, UserLogService userLogService) {
        this.dashboardFolderRepository = dashboardFolderRepository;
        this.dashboardReportRepository = dashboardReportRepository;
        this.userLogService = userLogService;
    }

    @Override
    public List<DashboardFolderDto> getDashboardFolderByFolderId(List<Long> folderIds) {
        if (folderIds.isEmpty()) {
            folderIds = dashboardFolderRepository.getFolderParentId();
        }
        return super.mapList(
                dashboardFolderRepository.findByFolderIdInAndStatus(folderIds, Const.STATUS.ACTIVE),
                DashboardFolderDto.class);
    }

    @Override
    public List<DashboardFolderDto> getDashboardFolderTree(DashboardFolderDto dto) {
        return dashboardFolderRepository.getDashboardFolderTrees(dto);
    }

    @Override
    public Page<DashboardFolderDto> doSearch(DashboardFolderDto dto, Pageable pageable) {
        Page<DashboardFolderDto> result = dashboardFolderRepository.doSearch(dto, pageable);
        //ghi log
//        UserLogDto userLogDto = new UserLogDto("GET", "SEARCH QUAN_LY_FOLDER", "Tìm kiếm quản lý thư mục");
//        userLogService.saveLog(userLogDto);
        return result;
    }

    @Override
    public DashboardFolderDto findById(Long id) {
        return dashboardFolderRepository.findById(id)
                .map(df -> super.map(df, DashboardFolderDto.class))
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "folderId"));
    }

    @Override
    public DashboardFolderDto create(DashboardFolderDto dto) {
        if (Objects.nonNull(dto.getFolderId())) {
            throw new ServerException(ErrorCode.ALREADY_EXIST, DASHBOARD_FOLDER);
        }
        dto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.INSERT));
        this.validateSave(dto);
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "CREATE QUAN_LY_FOLDER", MessageUtil.getMessage("code.quan_ly_folder.create"), objectToJson(dto));
        userLogService.saveLog(userLogDto);
        return this.save(dto);
    }

    @Override
    public DashboardFolderDto update(DashboardFolderDto dto) {
        if (Objects.isNull(dto.getFolderId())) {
            throw new ServerException(ErrorCode.NOT_FOUND, DASHBOARD_FOLDER);
        }
        dto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.UPDATE));
        this.validateSave(dto);
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "UPDATE QUAN_LY_FOLDER", MessageUtil.getMessage("code.quan_ly_folder.update"), objectToJson(dto));
        userLogService.saveLog(userLogDto);
        return this.save(dto);
    }

    @Override
    public DashboardFolderDto changeFolderStatus(DashboardFolderDto dto) {
        if (null == dto.getFolderId()) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "folderId");
        }
        if (null == dto.getStatus()) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "status");
        }
        //ghi log
        UserLogDto userLogDto = null;
        if (dto.getStatus() == 0) {
            userLogDto = new UserLogDto("POST", "LOCK_UP QUAN_LY_FOLDER", MessageUtil.getMessage("code.quan_ly_folder.lock_up"), objectToJson(dto));
        } else if (dto.getStatus() == 1) {
            userLogDto = new UserLogDto("POST", "UNLOCK QUAN_LY_FOLDER", MessageUtil.getMessage("code.quan_ly_folder.unlock"), objectToJson(dto));
        }

        userLogService.saveLog(userLogDto);
        return dashboardFolderRepository.findById(dto.getFolderId())
                .map(dfe -> {
                    String oldValue = super.toJson(dfe);
                    dfe.setStatus(dto.getStatus());
                    DashboardFolderEntity newValue = dashboardFolderRepository.save(dfe);
                    super.saveLog(super.updateLog(Const.TABLE.DASHBOARD_FOLDER, newValue.getFolderId(), oldValue, newValue));
                    return super.map(newValue, DashboardFolderDto.class);
                })
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, DASHBOARD_FOLDER));
    }

    @Override
    public List<TreeDto> getFolderAndFile() {
        List<DashboardReportDto> dashboardReportDtos = super.mapList(dashboardReportRepository.findAll(), DashboardReportDto.class);
        return dashboardFolderRepository.getDashboardFolderTree(new DashboardFolderDto())
                .stream().peek(td -> this.loadDashboardReport(td, dashboardReportDtos))
                .collect(Collectors.toList());
    }

    private void validateSave(DashboardFolderDto dto) {
        dashboardFolderRepository.findByFolderCode(dto.getFolderCode()).stream()
                .findFirst()
                .filter(ce -> !ce.getFolderId().equals(dto.getFolderId()))
                .ifPresent(cer -> {
                    throw new ServerException(ErrorCode.ALREADY_EXIST, DASHBOARD_FOLDER);
                });
        if (null != dto.getFolderParentId()) {
            dashboardFolderRepository.findById(dto.getFolderParentId())
                    .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "folderParentId"));
        }
    }

    private DashboardFolderDto save(DashboardFolderDto dto) {
        dto.setModifiedDate(new Date());
        dto.setUpdateUser(super.getCurrentUsername());
        if (Objects.isNull(dto.getStatus())) {
            dto.setStatus(Const.STATUS.ACTIVE);
        }
        ActionAuditDto.Builder logDashboardFolder = dto.getLogBuilder()
                .tableName(Const.TABLE.DASHBOARD_FOLDER)
                .oldValue(null != dto.getFolderId()
                        ? dashboardFolderRepository.findById(dto.getFolderId()).orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "dashboard folder"))
                        : null);
        DashboardFolderEntity entity = dashboardFolderRepository.save(super.map(dto, DashboardFolderEntity.class));
        super.saveLog(logDashboardFolder
                .newValue(entity)
                .objectId(entity.getFolderId())
                .build());
        return super.map(entity, DashboardFolderDto.class);
    }

    private void loadDashboardReport(TreeDto treeDto, List<DashboardReportDto> dtos) {
        List<DashboardReportDto> result = dtos.stream()
                .filter(dr -> dr.getFolderId().equals(treeDto.getId()))
                .collect(Collectors.toList());
        treeDto.setDashboardReports(result);
        dtos.removeAll(result);
    }
}
