package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.ActionAuditDto;
import com.lifesup.gbtd.dto.object.ParamsReportDto;
import com.lifesup.gbtd.dto.object.DashboardReportDto;
import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.DashboardReportEntity;
import com.lifesup.gbtd.repository.DashboardFolderRepository;
import com.lifesup.gbtd.repository.DashboardReportRepository;
import com.lifesup.gbtd.service.inteface.IDashboardReportService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.FileUtil;
import com.lifesup.gbtd.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.birt.core.script.CoreJavaScriptInitializer;
import org.eclipse.birt.core.script.function.bre.BirtDateTime;
import org.eclipse.birt.core.script.function.bre.BirtDateTimeFunctionFactory;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional
public class DashboardReportService extends BaseService implements IDashboardReportService {

    private final DashboardReportRepository dashboardReportRepository;
    private final DashboardFolderRepository dashboardFolderRepository;
    private final UserLogService userLogService;
    
    private static final String DASHBOARD_REPORT = "Dashboard Report";

    @Autowired
    public DashboardReportService(DashboardReportRepository dashboardReportRepository, DashboardFolderRepository dashboardFolderRepository, UserLogService userLogService) {
        this.dashboardReportRepository = dashboardReportRepository;
        this.dashboardFolderRepository = dashboardFolderRepository;
        this.userLogService = userLogService;
    }

    @Override
    public Page<DashboardReportDto> doSearch(DashboardReportDto dto, Pageable pageable) {
        //ghi log
//        UserLogDto userLogDto = new UserLogDto("GET", "SEARCH QUAN_LY_BAO_CAO", "Tìm kiếm quản lý báo cáo");
//        userLogService.saveLog(userLogDto);
        return dashboardReportRepository.doSearch(dto, pageable);
    }

    @Override
    public DashboardReportDto findById(Long id) {
        return dashboardReportRepository.findById(id)
                .map(dr -> {
                    DashboardReportDto dto = super.map(dr, DashboardReportDto.class);
                    dto.setParamsReportDtos(executeParamReport(dto));
                    return dto;
                })
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "reportId"));
    }

    @Override
    public DashboardReportDto create(DashboardReportDto dto) {
        if (Objects.nonNull(dto.getReportId())) {
            throw new ServerException(ErrorCode.ALREADY_EXIST, DASHBOARD_REPORT);
        }
        dto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.INSERT));
        this.validateSave(dto);
        //ghi log
        DashboardReportDto obj = new DashboardReportDto(dto.getReportId(),dto.getReportCode(),dto.getReportName(),dto.getFolderId(),dto.getFileName(),dto.getSplitSheet());
        UserLogDto userLogDto = new UserLogDto("POST", "CREATE QUAN_LY_BAO_CAO", MessageUtil.getMessage("code.quan_ly_bao_cao.create"), objectToJson(obj));
        userLogService.saveLog(userLogDto);
        return this.save(dto);
    }

    @Override
    public DashboardReportDto update(DashboardReportDto dto) {
        if (Objects.isNull(dto.getReportId())) {
            throw new ServerException(ErrorCode.NOT_FOUND, DASHBOARD_REPORT);
        }
        dto.setLogBuilder(super.defaultLogBuilder().action(Const.ACTION.UPDATE));
        this.validateSave(dto);
        //ghi log
        DashboardReportDto obj = new DashboardReportDto(dto.getReportId(),dto.getReportCode(),dto.getReportName(),dto.getFolderId(),dto.getFileName(),dto.getSplitSheet());
        UserLogDto userLogDto = new UserLogDto("POST", "UPDATE QUAN_LY_BAO_CAO", MessageUtil.getMessage("code.quan_ly_bao_cao.update"), objectToJson(obj));
        userLogService.saveLog(userLogDto);
        return this.save(dto);
    }

    @Override
    public DashboardReportDto changeFolderStatus(DashboardReportDto dto) {
        if (null == dto.getReportId()) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "reportId");
        }
        if (null == dto.getStatus()) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "status");
        }
        //ghi log
        UserLogDto userLogDto = null;
        if (dto.getStatus() == 0) {
            userLogDto = new UserLogDto("POST", "LOCK_UP QUAN_LY_BAO_CAO", MessageUtil.getMessage("code.quan_ly_bao_cao.lock_up"), objectToJson(dto));
            userLogService.saveLog(userLogDto);
        } else if (dto.getStatus() == 1) {
            userLogDto = new UserLogDto("POST", "UNLOCK QUAN_LY_BAO_CAO", MessageUtil.getMessage("code.quan_ly_bao_cao.unlock"), objectToJson(dto));
            userLogService.saveLog(userLogDto);
        }
        return dashboardReportRepository.findById(dto.getReportId())
                .map(dre -> {
                    String oldValue = super.toJson(dre);
                    dre.setStatus(dto.getStatus());
                    DashboardReportEntity newValue = dashboardReportRepository.save(dre);
                    super.saveLog(super.updateLog(Const.TABLE.DASHBOARD_REPORT, newValue.getReportId(), oldValue, newValue));
                    return super.map(newValue, DashboardReportDto.class);
                })
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, DASHBOARD_REPORT));
    }

    private void validateSave(DashboardReportDto dto) {
        if (null != dto.getReportId()) {
            // update
            if (null != dto.getFile()
                    && StringUtils.isNotEmpty(dto.getFileName())
                    && StringUtils.isNotEmpty(dto.getFile().getOriginalFilename())
                    && !dto.getFile().getOriginalFilename().endsWith("." + Const.FILE_TYPE.BIRT_REPORT)) {
                throw new ServerException(ErrorCode.NOT_VALID, "file type");
            }
        } else {
            // new
            if (StringUtils.isEmpty(dto.getFileName())) {
                throw new ServerException(ErrorCode.MISSING_PARAMS, "fileName");
            }
            if (null == dto.getFile()) {
                throw new ServerException(ErrorCode.MISSING_PARAMS, "file");
            }
            if (StringUtils.isNotEmpty(dto.getFile().getOriginalFilename())
                    && !dto.getFile().getOriginalFilename().endsWith("." + Const.FILE_TYPE.BIRT_REPORT)) {
                throw new ServerException(ErrorCode.NOT_VALID, "file type");
            }
        }
        dashboardReportRepository.findByReportCode(dto.getReportCode()).stream()
                .findFirst()
                .filter(rc -> !rc.getReportId().equals(dto.getReportId()))
                .ifPresent(rcr -> {
                    throw new ServerException(ErrorCode.ALREADY_EXIST, DASHBOARD_REPORT);
                });
        dashboardFolderRepository.findById(dto.getFolderId())
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "dashboard folder"));
    }

    private DashboardReportDto save(DashboardReportDto dto) {
        dto.setModifiedDate(new Date());
        dto.setUpdateUser(super.getCurrentUsername());
        dto.setIpServer(FileUtil.getInstance().getServerInfo().getHost());
        if (Objects.isNull(dto.getStatus())) {
            dto.setStatus(Const.STATUS.ACTIVE);
        }
        ActionAuditDto.Builder logDashBoardReport = dto.getLogBuilder()
                .tableName(Const.TABLE.DASHBOARD_REPORT);

        boolean saveNewFile = true;
        // xoa file cu khi update
        String fileName;
        if (null != dto.getReportId()) {
            DashboardReportEntity old = dashboardReportRepository.findById(dto.getReportId())
                    .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, DASHBOARD_REPORT));
            logDashBoardReport.oldValue(old);
            if (null != dto.getFile()) {
                try {
                    boolean result = Files.deleteIfExists(FileUtil.getInstance().getFolderReport(old.getFileName()));
                    log.info("delete file {}: {}", old.getFileName(), result);
                } catch (Exception e) {
                    log.error("loi khi xoa", e);
                    throw new ServerException(ErrorCode.FAILED, "delete old file");
                }
                fileName = StringUtils.replace(
                        dto.getFileName(),
                        Const.SPECIAL_CHAR.DOT + Const.FILE_TYPE.BIRT_REPORT,
                        "-" + System.currentTimeMillis() + Const.SPECIAL_CHAR.DOT + Const.FILE_TYPE.BIRT_REPORT);
            } else {
                saveNewFile = false;
                fileName = old.getFileName();
            }
        } else {
            fileName = StringUtils.replace(
                    dto.getFileName(),
                    Const.SPECIAL_CHAR.DOT + Const.FILE_TYPE.BIRT_REPORT,
                    "-" + System.currentTimeMillis() + Const.SPECIAL_CHAR.DOT + Const.FILE_TYPE.BIRT_REPORT);
        }
        dto.setFileName(fileName);

        DashboardReportEntity entity = dashboardReportRepository.save(super.map(dto, DashboardReportEntity.class));
        super.saveLog(logDashBoardReport
                .newValue(entity)
                .objectId(entity.getReportId())
                .build());
        DashboardReportDto responseData = super.map(entity, DashboardReportDto.class);

        // save file report
        if (saveNewFile) {
            FileUtil.getInstance().writeFileReport(dto.getFile(), fileName).getFullPath();
        }

        return responseData;
    }

    private List<ParamsReportDto> executeParamReport(DashboardReportDto dto) {
        List<ParamsReportDto> paramsReportDtos = new ArrayList<>();
        IReportEngine engine = TestReportService.getEngineInstance();
        IReportRunnable design = null;

        try {
            URL url = new URL(FileUtil.getInstance().getPathUrlFIle(dto.getIpServer(), dto.getFileName()));
            design = engine.openReportDesign(url.openStream());
        } catch (IOException | EngineException e) {
            log.error("error", e);
            throw new ServerException(ErrorCode.FAILED, "file report");
        }
        IGetParameterDefinitionTask task = engine.createGetParameterDefinitionTask(design);
        Collection params = task.getParameterDefns(true);
        Iterator iter = params.iterator();
        while (iter.hasNext()) {
            IParameterDefnBase param = (IParameterDefnBase) iter.next();
            IScalarParameterDefn scalar = (IScalarParameterDefn) param;
            paramsReportDtos.add(loadParameterDetails(scalar));
        }
        task.close();
//        engine.destroy();
        return paramsReportDtos;
    }

    private ParamsReportDto loadParameterDetails(IScalarParameterDefn scalar) {
        ParamsReportDto paramsReportDto = new ParamsReportDto();
        paramsReportDto.setDataType(Const.DATA_TYPE_MAP.get(scalar.getSelectionList().size() > 0 ?
                "dept_para_tree".equals(scalar.getName()) ? 10 : 9 : scalar.getDataType()));
        paramsReportDto.setParamName(scalar.getName());
        paramsReportDto.setPromptText(scalar.getPromptText());
        paramsReportDto.setHidden(scalar.isHidden());
        paramsReportDto.setSelectionList(scalar.getSelectionList());
        paramsReportDto.setDisplayFormat(scalar.getDisplayFormat());
        if (7 == scalar.getDataType() && null != scalar.getDefaultValue()) {
            Context cx = Context.enter();
            Scriptable scope = cx.initStandardObjects();
            new CoreJavaScriptInitializer().initialize( cx, scope );
            paramsReportDto.setDefaultValue(cx.evaluateString(scope, "BirtDateTime.firstDayOfMonth(BirtDateTime.addMonth(BirtDateTime.today(),-1))", "inline", 1, null));
        } else {
            paramsReportDto.setDefaultValue(scalar.getDefaultValue());
        }
        return paramsReportDto;
    }
}
