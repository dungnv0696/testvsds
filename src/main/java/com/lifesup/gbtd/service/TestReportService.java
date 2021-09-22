package com.lifesup.gbtd.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.dto.response.ResponseCommon;
import com.lifesup.gbtd.exception.BirtHandleException;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.TempReportEntity;
import com.lifesup.gbtd.repository.ParamTreeRepository;
import com.lifesup.gbtd.repository.TempReportRepository;
import com.lifesup.gbtd.service.inteface.ILogActionService;
import com.lifesup.gbtd.service.inteface.ITestReportService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.birt.report.engine.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

@Service
@Slf4j
public class TestReportService extends BaseService implements ITestReportService {

//    private static Logger log = LoggerFactory.getLogger(TestReportService.class);

    @Value("${report.excel}")
    private String PATH_EXCEL_REPORT;

    @Value("${report.html}")
    private String PATH_HTML_REPORT;

    @Value("${report.pdf}")
    private String PATH_PDF_REPORT;

    private static String PATH_BIRT_RUNTIME;

    @Value("${report.url}")
    private String PATH_REPORT_TEMPLATE;

    @Value("${report.folder}")
    private String PATH_REPORT_FOLDER;

    private static final String REPORT_TYPE_EXCEL = "2";
    private static final String REPORT_TYPE_HTML = "1";
    private static final String REPORT_TYPE_PDF = "3";
    private static final String REPORT_TYPE_EXCEL_ALL = "-1";


    private final UserLogService userLogService;

    private static IReportEngine engine = null;
    private static EngineConfig reportEngineConf;
    private final ParamTreeRepository paramTreeRepository;
    private final TempReportRepository tempReportRepository;
    private final ILogActionService logActionService;

    @Autowired
    public TestReportService(UserLogService userLogService, ParamTreeRepository paramTreeRepository, TempReportRepository tempReportRepository,
                             ILogActionService logActionService) {
        this.userLogService = userLogService;
        this.paramTreeRepository = paramTreeRepository;
        this.tempReportRepository = tempReportRepository;
        this.logActionService = logActionService;
    }

    @Value("${report.run}")
    public static void setPathBirtRuntime(String pathBirtRuntime) {
        PATH_BIRT_RUNTIME = pathBirtRuntime;
    }

    @Override
    public ResponseCommon generateReport(ReportDto dataSearch) {
        String reportName = "";
        String outputPath = null;
        RenderOption options = null;
        RenderOption context = null;
        String tableId = null;
        switch (dataSearch.getCode()) {
            case REPORT_TYPE_EXCEL:
                reportName = Const.reportNameExcel;
                if (REPORT_TYPE_EXCEL_ALL.equals(dataSearch.getTreeTarget())) reportName = Const.reportNameExcelAll;
                outputPath = String.format(PATH_EXCEL_REPORT, dataSearch.getReportName());
                options = new EXCELRenderOption();
                options.setOutputFileName(outputPath);
                options.setOutputFormat("xlsx");
                context = new EXCELRenderOption();
                tableId = Const.reportShowTable;
                break;
            case REPORT_TYPE_HTML:
                reportName = Const.reportNameHtml;
                if (REPORT_TYPE_EXCEL_ALL.equals(dataSearch.getTreeTarget())) reportName = Const.reportNameHtmlAll;
                outputPath = String.format(PATH_HTML_REPORT, dataSearch.getReportName());
                options = new HTMLRenderOption();
                options.setOutputFileName(outputPath);
                options.setOutputFormat("html");
                context = new HTMLRenderOption();
                ((HTMLRenderOption) context).setImageDirectory("images");
                tableId = Const.reportCheckTable;
                break;
            case REPORT_TYPE_PDF:
                reportName = Const.reportNameExcel;
                if (REPORT_TYPE_EXCEL_ALL.equals(dataSearch.getTreeTarget())) reportName = Const.reportNamePdfAll;
                outputPath = String.format(PATH_PDF_REPORT, dataSearch.getReportName());
                options = new PDFRenderOption();
                options.setOutputFileName(outputPath);
                options.setOutputFormat("pdf");
                context = new PDFRenderOption();
                tableId = Const.reportShowTable;
                break;
            default:
                ResponseCommon res = new ResponseCommon();
                res.setContent("");
                return res;
        }
        Map<String, Object> inputParams;
        if (DataUtil.isNullObject(dataSearch.getIpServer()) && DataUtil.isNullObject(dataSearch.getFileName())) {
            inputParams = new HashMap();
            if (!DataUtil.isNullObject(dataSearch.getFromDate())) {
                inputParams.put(Const.reportFromDate, new java.sql.Date(dataSearch.getFromDate().getTime()));
            }
            inputParams.put(Const.reportTreeTaget, dataSearch.getTreeTarget());inputParams.put(Const.reportDeptPara, dataSearch.getDeptParam());inputParams.put(Const.reportType, dataSearch.getReportType());inputParams.put(Const.typeReportN, dataSearch.getType());
            for (int i = 1; i < 10; i++) {
                inputParams.put(tableId + i, this.checkTable(String.valueOf(i), dataSearch.getChecked()));
            }
            return this.doRender(new ParamHolder.Builder().setReportName(reportName).setOutputPath(outputPath).setInputParam(inputParams).setContextMap(Collections.singletonMap(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT, context)).setRenderOption(options).build(), reportName);
        } else {
            try {
                inputParams = new ObjectMapper().readValue(dataSearch.getMapValue(), Map.class);
                for (Map.Entry<String, Object> entry : inputParams.entrySet()) {
                    try {
                        entry.setValue(new java.sql.Date(new Date(entry.getValue().toString()).getTime()));
                    } catch (Exception e) {
                        entry.setValue(entry.getValue());
                        log.error("loi parse DATE", e);
                    }
                }
                return this.doRenderFileReport(new ParamHolder.Builder().setReportName(reportName).setOutputPath(outputPath).setInputParam(inputParams).setContextMap(Collections.singletonMap(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT, context)).setRenderOption(options).build(), dataSearch.getIpServer(), dataSearch.getFileName(), reportName);
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("loi parse JSON", ex);
                return new ResponseCommon();
            }
        }
    }

    private ResponseCommon doRender(ParamHolder holder, String reportName) {
        ResponseCommon res = new ResponseCommon();
        File fileOld;
        try {
            fileOld = new File(holder.getOutputPath());
            fileOld.delete();

            IReportEngine engine = getEngineInstance();
            IReportRunnable design = engine.openReportDesign(PATH_REPORT_TEMPLATE + holder.getReportName());
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            task.setParameterValues(holder.getInputParam());
            task.setAppContext(holder.getContextMap());
            task.setRenderOption(holder.getRenderOption());

            task.run();
        } catch (EngineException e) {
            res.setErrorCode(Const.ERR_02);
            log.error("error with engine", e);
        } catch (Exception e) {
            log.error("Unexpected", e);
        } finally {
            log.info("done running report!");
        }
        res.setContent(reportName);
        if (StringUtils.isNotEmpty(reportName)) {
            if (DataUtil.isNullObject(res.getErrorCode())) {
                res.setErrorCode(Const.SUCCESS);
            }
            res.setContent(reportName);
        } else {
            res.setErrorCode(Const.ERROR);
        }
        return res;
    }

    private ResponseCommon doRenderFileReport(ParamHolder holder, String ipServer, String fileName, String reportName) {
        ResponseCommon res = new ResponseCommon();
        BirtHandleException birtException = new BirtHandleException();
        IRunAndRenderTask task = null;
        String message = null;
        File fileOld;
        try {
            fileOld = new File(holder.getOutputPath());
            fileOld.delete();

            IReportEngine engine = getEngineInstance();
            URL url = new URL(FileUtil.getInstance().getPathUrlFIle(ipServer, fileName));
            IReportRunnable design = engine.openReportDesign(url.openStream());
            task = engine.createRunAndRenderTask(design);
            task.setParameterValues(holder.getInputParam());
            task.setAppContext(holder.getContextMap());
            task.setRenderOption(holder.getRenderOption());
            task.setErrorHandlingOption(IEngineTask.CANCEL_ON_ERROR);
            task.run();
        } catch (EngineException e) {
            message = e.getMessage();
            log.error("error with engine", e);
        } catch (FileNotFoundException e2) {
            message = e2.getMessage();
            res.setErrorCode(Const.ERR_02);
            log.error("file not found", e2);
        } catch (Exception e1) {
            message = e1.getMessage();
            log.error("Unexpected", e1);
        } finally {
            log.info("done running report!");
            if (DataUtil.isNullObject(message)) {
                res = birtException.handleException(task);
            } else {
                if (DataUtil.isNullObject(res.getErrorCode())) {
                    res.setErrorCode(Const.ERROR);
                }
                res.setErrorMessage(message);
            }
//            task.close();
        }
        res.setContent(reportName);
        return res;
    }

    public static IReportEngine getEngineInstance() {
        if (engine == null) {
            // engine config for repot
            reportEngineConf = new EngineConfig();
            reportEngineConf.setEngineHome(PATH_BIRT_RUNTIME);
            reportEngineConf.setLogConfig(null, Level.FINE);
            engine = new ReportEngine(reportEngineConf);
        }
        return engine;
    }

    @Override
    public List<ParamTreeDto> getUnit(ReportDto reportDTO) {
        reportDTO.setDeptId(super.getCurrentUserDeptId());
        return paramTreeRepository.getListUnit(reportDTO);
    }
    @Override
    public List<ParamTreeDto> getUnitDeptTree(ReportDto reportDTO) {
//        reportDTO.setDeptId(super.getCurrentUserDeptId());
        return paramTreeRepository.getListUnitDeptTree(reportDTO);
    }

    @Override
    public List<TempReportDto> getTempReport(String userName) {
        return paramTreeRepository.getListTempReport(userName);
    }

    @Override
    public void updateTempReport(TempReportDto tempReportDTO) {
        paramTreeRepository.updateTempReport(tempReportDTO);
    }

    @Override
    public void insertTempReport(TempReportDto tempReportDTO, String name) {
        tempReportDTO.setReportName(tempReportDTO.getReportName().trim().replaceAll("\\s+", " "));
        List<TempReportDto> list = paramTreeRepository.findTempReportByName(tempReportDTO.getReportName(), name);
        if (list.size() > 0) {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date(System.currentTimeMillis());
            tempReportDTO.setReportName(tempReportDTO.getReportName() + " (" + dateFormat.format(date) + ")");
        }
        paramTreeRepository.insertTempReport(tempReportDTO, name);
    }

    @Override
    public void deleteTempReport(TempReportDto tempReportDTO) {
        TempReportEntity tempReport = tempReportRepository.findById(tempReportDTO.getReportId())
                .orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, "tempReport"));
        int result = paramTreeRepository.deleteTempReport(tempReportDTO);
        if (result == 0) {
            throw new ServerException(ErrorCode.FAILED, "DELETE");
        }
        logActionService.saveLogActionInternal(
                super.createLogDto(Const.TABLE.TEMP_REPORT, Const.ACTION.DELETE, tempReportDTO.getReportId(), tempReport, null));
    }

    @Override
    public List<SheetDto> getSheet() {
        return paramTreeRepository.getSheet();
    }


    private boolean checkTable(String code, List<CheckedDto> lst) {
        if (lst == null || lst.isEmpty()) {
            return false;
        }

        for (CheckedDto obj : lst) {
            if (code.equals(obj.getCode())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ResponseEntity<Resource> downloadReport(String fileName) {
        try {
            Path path = Paths.get(PATH_REPORT_FOLDER + fileName);
            if (!Files.exists(path)) {
                throw new ServerException(ErrorCode.NOT_FOUND, "report template");
            }

            InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ);
            InputStreamResource resource = new InputStreamResource(inputStream);

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(Files.size(path)));

            return ResponseEntity.ok()
                    .headers(header)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            log.error("not found", e);
        } catch (Exception e) {
            log.error("unexpected", e);
        }
        return null;
    }

//    public void setReportDAO(TestReportDAO reportDAO) {
//        this.reportDAO = reportDAO;
//    }


//    @Override
//    public String genarateReport(ReportDto dataSearch) {
//        String reportName = "";
//        if ("2".equals(dataSearch.getCode())) {
//
//            reportName = Const.reportNameExcel;
//            //"bao_cao_n2_excel.rptdesign";
//            if("-1".equals(dataSearch.getTreeTarget())){
//                reportName = Const.reportNameExcelAll;
//                //"bao_cao_n2_excel_all.rptdesign";
//            }
//
//            try {
//                File fileOld = new File(PATH_EXCEL_REPORT);
//                fileOld.delete();
//            } catch (Exception ex) {
//                logger.info(ex.getMessage(),ex);
//            }
//            IRunAndRenderTask task;
//            EXCELRenderOption renderContext = null;
//            HashMap contextMap = null;
//            EXCELRenderOption options = null;
//            EngineConfig conf = new EngineConfig();
//            conf.setEngineHome(PATH_BIRT_RUNTIME);
//            conf.setLogConfig(null, Level.FINE);
//            try {
//                Platform.startup(conf);
//            } catch (BirtException e1) {
//                logger.info(e1.getMessage(),e1);
//            }
//            IReportEngine engine = new ReportEngine(conf);
//
//            HashMap inputParam = new HashMap();
//
//            Date sqlDate = new Date(dataSearch.getFromDate().getTime());
//            inputParam.put(Const.reportFromDate, sqlDate);
//
//            IReportRunnable design = engine.openReportDesign(PATH_REPORT_TEMPLATE + reportName);
//
//            task = engine.createRunAndRenderTask(design);
//            inputParam.put(Const.reportDeptPara, dataSearch.getDeptParam());
//            inputParam.put(Const.reportType, dataSearch.getReportType());
//            for (int i = 1; i < 10; i++) {
//                if (!this.checkTable(String.valueOf(i), dataSearch.getChecked())) {
//                    inputParam.put(Const.reportShowTable + i, false);
//                } else {
//                    inputParam.put(Const.reportShowTable + i, true);
//                }
//            }
//            inputParam.put(Const.reportTreeTaget, dataSearch.getTreeTarget());
//
//            task.setParameterValues(inputParam);
//            renderContext = new EXCELRenderOption();
//            contextMap = new HashMap();
//            contextMap.put(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT, renderContext);
//            task.setAppContext(contextMap);
//            options = new EXCELRenderOption();
//            options.setOutputFileName(PATH_EXCEL_REPORT);
//            options.setOutputFormat("xlsx");
//
//            task.setRenderOption(options);
//
//            try {
//                task.run();
//                task.close();
//            } catch (Exception e) {
//                logger.info(e.getMessage(),e);
//            }
//            engine.destroy();
//
//        }
//
//        if ("1".equals(dataSearch.getCode())) {
//
//            reportName = Const.reportNameHtml;
//            if("-1".equals(dataSearch.getTreeTarget())){
//                reportName = Const.reportNameHtmlAll;
//            }
//            try {
//                File fileOld = new File(PATH_HTML_REPORT);
//                fileOld.delete();
//            } catch (Exception ex) {
//                logger.info(ex.getMessage(), ex);
//            }
//            IRunAndRenderTask task;
//            HTMLRenderOption renderContext = null;
//            HashMap contextMap = null;
//            HTMLRenderOption options = null;
//
//            EngineConfig conf = new EngineConfig();
//            conf.setEngineHome(PATH_BIRT_RUNTIME);
//            conf.setLogConfig(null, Level.FINE);
//            try {
//                Platform.startup(conf);
//            } catch (BirtException e1) {
//                logger.info(e1.getMessage(),e1);
//            }
//
//            IReportEngine engine = new ReportEngine(conf);
//            IReportRunnable design = engine.openReportDesign(PATH_REPORT_TEMPLATE + reportName);
//            task = engine.createRunAndRenderTask(design);
//
//            HashMap inputParam = new HashMap();
//
//            Date sqlDate = new Date(dataSearch.getFromDate().getTime());
//            inputParam.put(Const.reportFromDate, sqlDate);
//
//            inputParam.put(Const.reportDeptPara, dataSearch.getDeptParam());
//            inputParam.put(Const.reportType, dataSearch.getReportType());
//            for (int i = 1; i < 10; i++) {
//                if (!this.checkTable(String.valueOf(i), dataSearch.getChecked())) {
//                    inputParam.put(Const.reportCheckTable + i, false);
//                } else {
//                    inputParam.put(Const.reportCheckTable + i, true);
//                }
//            }
//            inputParam.put(Const.reportTreeTaget, dataSearch.getTreeTarget());
//            task.setParameterValues(inputParam);
//            renderContext = new HTMLRenderOption();
//            renderContext.setImageDirectory("images");
//            contextMap = new HashMap();
//            contextMap.put(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT,
//                    renderContext);
//            task.setAppContext(contextMap);
//            options = new HTMLRenderOption();
//
//            options.setOutputFileName(PATH_HTML_REPORT); //output HTML will go to this folder
//            options.setOutputFormat("html");
//            task.setRenderOption(options);
//
//            try {
//                task.run();
//                task.close();
//            } catch (Exception e) {
//                logger.info(e.getMessage(), e);
//            }
//            engine.destroy();
//        }
//
//        if ("3".equals(dataSearch.getCode())) {
//            reportName = Const.reportNameExcelAll;
//            try {
//                File fileOld = new File(PATH_PDF_REPORT);
//                fileOld.delete();
//            } catch (Exception ex) {
//                logger.info(ex.getMessage(), ex);
//            }
//            IRunAndRenderTask task;
//            PDFRenderOption renderContext = null;
//            HashMap contextMap = null;
//            PDFRenderOption options = null;
//            EngineConfig conf = new EngineConfig();
//            conf.setEngineHome(PATH_BIRT_RUNTIME);
//            conf.setLogConfig(null, Level.FINE);
//            try {
//                Platform.startup(conf);
//            } catch (BirtException e1) {
//                logger.info(e1.getMessage(), e1);
//            }
//            IReportEngine engine = new ReportEngine(conf);
//
//            IReportRunnable design = engine.openReportDesign(PATH_REPORT_TEMPLATE + reportName);
//            task = engine.createRunAndRenderTask(design);
//            HashMap inputParam = new HashMap();
//            Date sqlDate = new Date(dataSearch.getFromDate().getTime());
//            inputParam.put(Const.reportFromDate, sqlDate);
//            inputParam.put(Const.reportDeptPara, dataSearch.getDeptParam());
//            inputParam.put(Const.reportType, dataSearch.getReportType());
//            inputParam.put(Const.reportType, dataSearch.getReportType());
//            for (int i = 1; i < 10; i++) {
//                if (!checkTable(String.valueOf(i), dataSearch.getChecked())) {
//                    inputParam.put(Const.reportShowTable + i, false);
//                } else {
//                    inputParam.put(Const.reportShowTable + i, true);
//                }
//            }
//            inputParam.put(Const.reportTreeTaget, dataSearch.getTreeTarget());
//            task.setParameterValues(inputParam);
//            renderContext = new PDFRenderOption();
//            contextMap = new HashMap();
//            contextMap.put(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT,
//                    renderContext);
//            task.setAppContext(contextMap);
//            options = new PDFRenderOption();
//            options.setOutputFileName(PATH_PDF_REPORT);
//            options.setOutputFormat("pdf");
//
//            task.setRenderOption(options);
//
//            try {
//                task.run();
//                task.close();
//            } catch (Exception e) {
//                logger.info(e.getMessage(),e);
//            }
//            engine.destroy();
//
//        }
//        return reportName;
//    }


    static class ParamHolder {
        private final String reportName;
        private final String outputPath;
        private final RenderOption renderOption;
        private final Map inputParam;
        private final Map contextMap;

        private ParamHolder(String reportName, String outputPath, RenderOption renderOption, Map inputParam, Map contextMap) {
            this.reportName = reportName;
            this.outputPath = outputPath;
            this.renderOption = renderOption;
            this.inputParam = inputParam;
            this.contextMap = contextMap;
        }

        public String getReportName() {
            return reportName;
        }

        public String getOutputPath() {
            return outputPath;
        }

        public RenderOption getRenderOption() {
            return renderOption;
        }

        public Map getInputParam() {
            return inputParam;
        }

        public Map getContextMap() {
            return contextMap;
        }

        static class Builder {

            private String reportName;
            private String outputPath;
            private RenderOption renderOption;
            private Map inputParam;
            private Map contextMap;

            public Builder() {
            }

            public Builder setReportName(String reportName) {
                this.reportName = reportName;
                return this;
            }

            public Builder setOutputPath(String outputPath) {
                this.outputPath = outputPath;
                return this;
            }

            public Builder setRenderOption(RenderOption renderOption) {
                this.renderOption = renderOption;
                return this;
            }

            public Builder setInputParam(Map inputParam) {
                this.inputParam = inputParam;
                return this;
            }

            public Builder setContextMap(Map contextMap) {
                this.contextMap = contextMap;
                return this;
            }

            public ParamHolder build() {
                return new ParamHolder(reportName, outputPath, renderOption, inputParam, contextMap);
            }
        }
    }
}