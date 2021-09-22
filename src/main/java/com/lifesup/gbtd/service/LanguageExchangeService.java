package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.CatItemEntity;
import com.lifesup.gbtd.model.LanguageExchangeEntity;
import com.lifesup.gbtd.repository.CatItemRepository;
import com.lifesup.gbtd.repository.LanguageExchangeRepository;
import com.lifesup.gbtd.service.inteface.ILanguageExchangeService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.ExcelUtil;
import com.lifesup.gbtd.util.FileUtil;
import com.lifesup.gbtd.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.birt.report.data.adapter.i18n.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LanguageExchangeService extends BaseService implements ILanguageExchangeService {

    // 5mb
    private static final long MAX_FILE_SIZE = 5242880;

    private static final String TABLE_COLUMN_ID = "BUSINESS_ID";
    private static final String TABLE_COLUMN_VALUE = "LEE_VALUE";

    private static final Integer INDEX_STT = 0;
    private static final Integer INDEX_LEE_ID = 1;
    private static final Integer INDEX_BUSINESS_ID = 2;
    private static final Integer INDEX_DEFAULT_VALUE = 3;
    private static final Integer INDEX_LEE_VALUE = 4;
    private static final Integer INDEX_HEADER_B_TABLE = 5;
    private static final Integer INDEX_HEADER_LANG = 6;

    private static final Integer COLUMN_ERROR = 5;
    private final UserLogService userLogService;
    private final CatItemRepository catItemRepository;
    private final LanguageExchangeRepository languageExchangeRepository;
    private static ColumnProfile[] COLUMN_PROFILES;


    private static void initColumn() {
        if (COLUMN_PROFILES == null) {
            COLUMN_PROFILES = new ColumnProfile[]{
                    new ColumnProfile(1500, "STT", "A"),
                    new ColumnProfile(3000, "Lee Id", "B"),
                    new ColumnProfile(6000, Const.LANGUAGE_EXCHANGE.ID_DATA, "C"),
                    new ColumnProfile(8000, Const.LANGUAGE_EXCHANGE.DEFAULT_VALUE, "D"),
                    new ColumnProfile(8000, Const.LANGUAGE_EXCHANGE.VALUE, "E"),
                    new ColumnProfile(0, Const.LANGUAGE_EXCHANGE.MAJOR_TABLE, "D"),
                    new ColumnProfile(0, Const.LANGUAGE_EXCHANGE.LANGUAGE, "D"),
                    new ColumnProfile(8000, Const.LANGUAGE_EXCHANGE.RESULT, "F"),
            };
        }
    }

    @Autowired
    public LanguageExchangeService(UserLogService userLogService, CatItemRepository catItemRepository, LanguageExchangeRepository languageExchangeRepository) {
        this.userLogService = userLogService;
        this.catItemRepository = catItemRepository;
        this.languageExchangeRepository = languageExchangeRepository;
    }

    @Override
    @Transactional
    public String exportExcel(LanguageExchangeDto dto) {
        List<LanguageExchangeDto> dtos = this.getDataExport(dto);
        String fileName = dto.getAppliedBusiness().trim() + "-" + dto.getLeeLocale().trim() + ".xlsx";

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Data");
        sheet.setDefaultRowHeightInPoints(21);
        sheet.setDefaultColumnWidth(25);

        // set header row
        this.createHeaderRow(sheet, workbook);
        this.createTableHeader(sheet, workbook);

        XSSFRow rowTableName = sheet.getRow(5);
        Cell cellTableNameVal = rowTableName.createCell(3);
        cellTableNameVal.setCellValue(dto.getAppliedBusiness());
        cellTableNameVal.setCellStyle(ExcelUtil.defaultStyleText(workbook));

        XSSFRow rowLEName = sheet.getRow(6);
        Cell cellLENameVal = rowLEName.createCell(3);
        cellLENameVal.setCellValue(dto.getLeeLocale());
        cellLENameVal.setCellStyle(ExcelUtil.defaultStyleText(workbook));

        // start from
        int rowNo = 9;
        XSSFRow row;
        for (LanguageExchangeDto le : dtos) {
            row = sheet.createRow(rowNo);
            this.createExcelRow(le, row, workbook);
            rowNo++;
        }

        //Hiding Columns
        sheet.setColumnHidden(1, true);

        try {
            fileName = FileUtil.getInstance().writeToFileOnServer(workbook, fileName).getFullPath();
        } catch (Exception ex) {
            log.error("error", ex);
            throw new ServerException(ErrorCode.FAILED, "Create File ");
        }
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "EXPORT_FILE DANH_MUC_DA_NGON_NGU", MessageUtil.getMessage("code.danh_muc_da_ngon_ngu.export_file"), objectToJson(dto));
        userLogService.saveLog(userLogDto);
        return fileName;
    }

    @Override
    @Transactional
    public FileImportDto<LanguageExchangeDto> importExcel(MultipartFile file, String appliedBusiness, String leeLocale) {
        if (null == file) {
            throw new ServerException(ErrorCode.NOT_VALID, "empty file");
        }
        if (!ExcelUtil.hasExcelFormat(file, MAX_FILE_SIZE)) {
            throw new ServerException(ErrorCode.NOT_VALID, "file type");
        }

        FileImportDto<LanguageExchangeDto> result = this.readFileAndValidate(file, appliedBusiness, leeLocale);
        if (result.getErrors().isEmpty()) {
            result.getData().forEach(e -> {
                List<LanguageExchangeEntity> exchangeEntities = languageExchangeRepository
                        .findByAppliedBusinessAndBusinessIdAndLeeLocale(e.getAppliedBusiness(), e.getBusinessId(), e.getLeeLocale());
                if (exchangeEntities.isEmpty()) {
                    languageExchangeRepository.save(super.map(e, LanguageExchangeEntity.class));
                } else {
                    LanguageExchangeEntity entity = super.map(e, LanguageExchangeEntity.class);
                    entity.setLeeId(exchangeEntities.get(0).getLeeId());
                    languageExchangeRepository.save(entity);
                }
            });
//            languageExchangeRepository.saveAll(super.mapList(result.getData(), LanguageExchangeEntity.class));
        }
        //ghi log
        UserLogDto userLogDto = new UserLogDto("POST", "IMPORT_FILE DANH_MUC_DA_NGON_NGU", MessageUtil.getMessage("code.danh_muc_da_ngon_ngu.import_file"));
        userLogService.saveLog(userLogDto);
        return result;
    }

    private FileImportDto<LanguageExchangeDto> readFileAndValidate(MultipartFile file, String appliedBusiness, String leeLocale) {
        initColumn();
        FileImportDto<LanguageExchangeDto> result;

        try (InputStream inputStream = file.getInputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            result = this.validateRow(workbook, sheet, appliedBusiness, leeLocale);

            if (!result.getErrors().isEmpty()) {
                XSSFRow resultRow = sheet.getRow(8);
                Cell newCell = resultRow.createCell(5);
                newCell.setCellValue(COLUMN_PROFILES[7].getTitle());
                newCell.setCellStyle(resultRow.getCell(4).getCellStyle());
                sheet.setColumnWidth(5, COLUMN_PROFILES[7].getWidth());
                String fileName = StringUtils.isNotEmpty(file.getOriginalFilename())
                        ? StringUtils.replace(file.getOriginalFilename(), ".", "-error.")
                        : "error.xlxs";
                fileName = FileUtil.getInstance().writeToFileOnServer(workbook, fileName).getFullPath();
                result.setFileName(fileName);
            }
        } catch (Exception ex) {
            log.error("error", ex);
            throw new ServerException(ErrorCode.SERVER_ERROR);
        }

        return result;
    }

    private FileImportDto<LanguageExchangeDto> validateRow(Workbook workbook, Sheet sheet, String appliedBusiness, String leeLocale) {
        initColumn();
        DataFormatter formatter = new DataFormatter();
        CellStyle textStyleErr = ExcelUtil.defaultStyleTextErr(workbook);List<LanguageExchangeDto> dtoList = new ArrayList<>();List<ExcelErrorDto> errorList = new ArrayList<>(), rowError;
        Set<Long> dtoSet = new HashSet<>();String appliedBusinessIn, leeLocaleIn;LanguageExchangeDto columnHolder = null;
        int rowCount = 0;
        for (Row row : sheet) {
            rowError = new ArrayList<>();rowCount++;LanguageExchangeDto dto = new LanguageExchangeDto();
            if (rowCount < 7) {
                if (rowCount == 4) {
                    appliedBusinessIn = formatter.formatCellValue(row.getCell(3)).trim();columnHolder = this.validateAppliedBusiness(appliedBusiness, appliedBusinessIn, rowCount, rowError);
                    if (!rowError.isEmpty()) {
                        ExcelUtil.createStyleCell(row, COLUMN_ERROR, textStyleErr).setCellValue(rowError.stream().map(ExcelErrorDto::getDetailError).collect(Collectors.joining(Const.SPECIAL_CHAR.COMMA)));
                        errorList.addAll(rowError);
                    }
                    continue;
                }
                if (rowCount == 5) {
                    leeLocaleIn = formatter.formatCellValue(row.getCell(3)).trim();leeLocale = this.validateLeeLocale(leeLocale, leeLocaleIn, rowCount, rowError);
                    if (!rowError.isEmpty()) {
                        ExcelUtil.createStyleCell(row, COLUMN_ERROR, textStyleErr).setCellValue(rowError.stream().map(ExcelErrorDto::getDetailError).collect(Collectors.joining(Const.SPECIAL_CHAR.COMMA)));
                        errorList.addAll(rowError);
                    }
                    continue;
                }
                if (!errorList.isEmpty()) break;
                continue;
            }
            String leeIdStr = formatter.formatCellValue(row.getCell(INDEX_LEE_ID)).trim(), businessIdStr = formatter.formatCellValue(row.getCell(INDEX_BUSINESS_ID)).trim(), defaultValue = formatter.formatCellValue(row.getCell(INDEX_DEFAULT_VALUE)).trim(), leeValue = formatter.formatCellValue(row.getCell(INDEX_LEE_VALUE)).trim();
            dto.setAppliedBusiness(appliedBusiness.toUpperCase());dto.setLeeLocale(leeLocale);dto.setDefaultValue(defaultValue);int rowCurrent = rowCount + 3;
            try {
                if (!StringUtils.isEmpty(leeIdStr)) {
                    Long leeId = Long.parseLong(leeIdStr);dto.setLeeId(leeId);
                }
            } catch (NumberFormatException ex) {
                ExcelErrorDto err = ExcelUtil.createError(rowCurrent, COLUMN_PROFILES[INDEX_LEE_ID], MessageUtil.getMessage("vi.err.is_number"));rowError.add(err);
            }
            if (StringUtils.isEmpty(businessIdStr)) {
                ExcelErrorDto err = ExcelUtil.createError(rowCurrent, COLUMN_PROFILES[INDEX_BUSINESS_ID], MessageUtil.getMessage("vi.err.not_empty"));rowError.add(err);
            } else {
                try {
                    Long businessId = Long.parseLong(businessIdStr);
                    dto.setBusinessId(businessId);
                    if (dtoSet.contains(dto.getBusinessId())) {
                        ExcelErrorDto err = ExcelUtil.createError(rowCurrent, COLUMN_PROFILES[INDEX_BUSINESS_ID], MessageUtil.getMessage("vi.err.exist"));
                        rowError.add(err);
                    } else {
                        dtoSet.add(dto.getBusinessId());
                    }
                    if (null != columnHolder) {
                        List list = languageExchangeRepository.checkExistData(appliedBusiness, columnHolder.getBusinessIdCol(), businessIdStr);
                        if (list.isEmpty()) {
                            ExcelErrorDto err = ExcelUtil.createError(rowCurrent, COLUMN_PROFILES[INDEX_BUSINESS_ID], MessageUtil.getMessage("vi.err.not_exist"));
                            rowError.add(err);dto.setBusinessIdCol(businessIdStr);dto.getErrorsMess().add(err.getDetailError());
                        }
                    }
                } catch (NumberFormatException ex) {
                    ExcelErrorDto err = ExcelUtil.createError(rowCurrent, COLUMN_PROFILES[INDEX_BUSINESS_ID], MessageUtil.getMessage("vi.err.is_number"));
                    rowError.add(err);dto.setBusinessIdCol(businessIdStr);
                }
            }
            if (StringUtils.isEmpty(leeValue)) {
                ExcelErrorDto err = ExcelUtil.createError(rowCurrent, COLUMN_PROFILES[INDEX_LEE_VALUE], MessageUtil.getMessage("vi.err.not_empty"));rowError.add(err);
            }
            dto.setLeeValue(leeValue);
            if (!rowError.isEmpty()) {
                ExcelUtil.createStyleCell(row, COLUMN_ERROR, textStyleErr).setCellValue(rowError.stream().map(ExcelErrorDto::getDetailError).collect(Collectors.joining(Const.SPECIAL_CHAR.COMMA)));errorList.addAll(rowError);
            }
            dtoList.add(dto);
        }
        FileImportDto<LanguageExchangeDto> result = new FileImportDto<>();
        result.setErrors(errorList);result.setData(dtoList);
        return result;
    }

    private LanguageExchangeDto validateAppliedBusiness(String appliedBusiness, String appliedBusinessIn, int row, List<ExcelErrorDto> errors) {
        initColumn();
        LanguageExchangeDto result = new LanguageExchangeDto();
        if (StringUtils.isEmpty(appliedBusinessIn)) {
            errors.add(ExcelUtil.createError(row, COLUMN_PROFILES[INDEX_HEADER_B_TABLE], MessageUtil.getMessage("vi.err.not_empty")));
            return result;
        }

        if (!appliedBusinessIn.toLowerCase().equals(appliedBusiness.trim().toLowerCase())) {
            errors.add(ExcelUtil.createError(row, COLUMN_PROFILES[INDEX_HEADER_B_TABLE], MessageUtil.getMessage("vi.err.not_map_table")));
            return result;
        }

        CatItemDto criteria = new CatItemDto();
        criteria.setCategoryCode(Const.CAT_ITEM_CODE.LANGUAGE_CODE);
        criteria.setItemCode(appliedBusinessIn);
        List<CatItemEntity> entities = catItemRepository.findAll(criteria);
        if (entities.isEmpty()) {
            errors.add(ExcelUtil.createError(row, COLUMN_PROFILES[INDEX_HEADER_B_TABLE], MessageUtil.getMessage("vi.err.not_exist")));
            return result;
        } else {
            List<CatItemEntity> configEntities = catItemRepository.findByParentItemId(entities.get(0).getItemId());
            List<CatItemEntity> configId = configEntities.stream()
                    .filter(e -> TABLE_COLUMN_ID.equals(e.getItemCode()))
                    .collect(Collectors.toList());

            List<CatItemEntity> configValue = configEntities.stream()
                    .filter(e -> TABLE_COLUMN_VALUE.equals(e.getItemCode()))
                    .collect(Collectors.toList());

            if ((configId.isEmpty() || configValue.isEmpty())
                    || (StringUtils.isEmpty(configId.get(0).getItemValue()) || StringUtils.isEmpty(configValue.get(0).getItemValue()))) {
                errors.add(ExcelUtil.createError(row, COLUMN_PROFILES[INDEX_HEADER_B_TABLE], MessageUtil.getMessage("vi.err.not_config_in_db")));
            } else {
                result.setBusinessIdCol(configId.get(0).getItemValue());
                result.setLeeValueCol(configValue.get(0).getItemValue());
            }
        }
        return result;
    }

    private String validateLeeLocale(String leeLocale, String leeLocaleIn, int row, List<ExcelErrorDto> rowError) {
        initColumn();
        if (StringUtils.isEmpty(leeLocaleIn)) {
            rowError.add(ExcelUtil.createError(row, COLUMN_PROFILES[INDEX_HEADER_LANG], MessageUtil.getMessage("vi.err.not_empty")));
            return null;
        }

        if (!leeLocaleIn.toLowerCase().equals(leeLocale.trim().toLowerCase())) {
            rowError.add(ExcelUtil.createError(row, COLUMN_PROFILES[INDEX_HEADER_LANG], MessageUtil.getMessage("vi.err.not_map_language")));
            return null;
        }

        CatItemDto criteria = new CatItemDto();
        criteria.setCategoryCode(Const.CAT_ITEM_CODE.LANGUAGE_CODE);
        criteria.setItemCode(leeLocaleIn);
        List<CatItemEntity> langs = catItemRepository.findAll(criteria);
        if (langs.isEmpty()) {
            rowError.add(ExcelUtil.createError(row, COLUMN_PROFILES[INDEX_HEADER_LANG], MessageUtil.getMessage("vi.err.not_exist")));
            return null;
        }
        return langs.get(0).getItemCode();
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        XSSFFont headerFont = (XSSFFont) workbook.createFont();
        headerFont.setFontName("Calibri");
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setBold(true);

        return ExcelUtil.defaultStyleHeaderCell((XSSFWorkbook) workbook, headerFont);
    }

    private List<LanguageExchangeDto> getDataExport(LanguageExchangeDto dto) {
        if (StringUtils.isEmpty(dto.getAppliedBusiness()) || StringUtils.isEmpty(dto.getLeeLocale())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "AppliedBusiness or Lee Locale");
        }
        CatItemDto criteria = new CatItemDto();
        criteria.setCategoryCodes(Collections.singletonList(Const.CAT_ITEM_CODE.BUSINESS_TABLE_COLUMN));
        criteria.setParentCategoryCodes(new String[]{Const.CAT_ITEM_CODE.BUSINESS_TABLE});
        criteria.setParentValue(dto.getAppliedBusiness());
        List<CatItemEntity> items = catItemRepository.findAll(criteria);
        CatItemEntity dtoBusinessId = items.stream()
                .filter(e -> TABLE_COLUMN_ID.equals(e.getItemCode()))
                .findAny()
                .orElse(null);
        CatItemEntity dtoLeeValue = items.stream()
                .filter(e -> TABLE_COLUMN_VALUE.equals(e.getItemCode()))
                .findAny()
                .orElse(null);

        if (null == dtoBusinessId || null == dtoLeeValue) {
            throw new ServerException(ErrorCode.NOT_FOUND, "config for table");
        }

        dto.setBusinessIdCol(dtoBusinessId.getItemValue());
        dto.setLeeValueCol(dtoLeeValue.getItemValue());

        return languageExchangeRepository.getLanguageExchanges(dto);
    }

    private void createHeaderRow(XSSFSheet sheet, Workbook workbook) {
        CellStyle headerStyle = this.createHeaderStyle(workbook);

        // set header row
        XSSFRow rowHeaderTD = sheet.createRow(0);
        rowHeaderTD.setHeightInPoints(21);

        Cell cellTitleTD = rowHeaderTD.createCell(0);
        cellTitleTD.setCellValue(Const.LANGUAGE_EXCHANGE.TITLE_TD);
        cellTitleTD.setCellStyle(headerStyle);
        sheet.autoSizeColumn(0);

        Cell cellTitleQH = rowHeaderTD.createCell(6);
        cellTitleQH.setCellValue(Const.LANGUAGE_EXCHANGE.TITLE_QH);
        cellTitleQH.setCellStyle(headerStyle);
        sheet.autoSizeColumn(6);

        XSSFRow rowHeaderBDL = sheet.createRow(1);
        rowHeaderBDL.setHeightInPoints(21);

        Cell cellTitleBDL = rowHeaderBDL.createCell(0);
        cellTitleBDL.setCellValue(Const.LANGUAGE_EXCHANGE.TITLE_BDL);
        cellTitleBDL.setCellStyle(headerStyle);

        Cell cellTitleTN = rowHeaderBDL.createCell(6);
        cellTitleTN.setCellValue(Const.LANGUAGE_EXCHANGE.TITLE_TN);
        cellTitleTN.setCellStyle(headerStyle);

        XSSFRow rowHeaderLE = sheet.createRow(3);
        rowHeaderLE.setHeightInPoints(30);

        XSSFFont headerFont = (XSSFFont) workbook.createFont();
        headerFont.setFontHeightInPoints((short) 13);
        headerFont.setBold(true);
        CellStyle titleStyle = this.createHeaderStyle(workbook);
        titleStyle.setFont(headerFont);
        Cell cellTitleLE = rowHeaderLE.createCell(3);
        cellTitleLE.setCellValue(Const.LANGUAGE_EXCHANGE.TITLE_DMDNN);
        cellTitleLE.setCellStyle(titleStyle);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 6, 8));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 8));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 3, 5));
    }

    private void createTableHeader(XSSFSheet sheet, Workbook workbook) {
        initColumn();
        CellStyle tableHeaderStyle = this.createHeaderStyle(workbook);
        ExcelUtil.borderAll(tableHeaderStyle);
        tableHeaderStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.index);
        tableHeaderStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        tableHeaderStyle.setFont(font);

        XSSFRow rowThTable = sheet.createRow(8);

        for (int i = 0; i < COLUMN_PROFILES.length - 3; i++) {
            Cell newCell = rowThTable.createCell(i);
            newCell.setCellValue(COLUMN_PROFILES[i].getTitle());
            newCell.setCellStyle(tableHeaderStyle);
            sheet.setColumnWidth(i, COLUMN_PROFILES[i].getWidth());
        }

        XSSFRow rowTableName = sheet.createRow(5);
        rowTableName.setHeightInPoints(21);

        Cell cellTableName = rowTableName.createCell(2);
        cellTableName.setCellValue(Const.LANGUAGE_EXCHANGE.TABLE_NAME);
        cellTableName.setCellStyle(tableHeaderStyle);

        XSSFRow rowLEName = sheet.createRow(6);
        rowLEName.setHeightInPoints(21);

        Cell cellLEName = rowLEName.createCell(2);
        cellLEName.setCellValue(Const.LANGUAGE_EXCHANGE.LANGUAGE);
        cellLEName.setCellStyle(tableHeaderStyle);
    }

    private void createExcelRow(LanguageExchangeDto dto, XSSFRow row, Workbook workbook) {
        CellStyle numberStyle = ExcelUtil.defaultStyleNumber(workbook);
        CellStyle textStyle = ExcelUtil.defaultStyleText(workbook);

        int col = 0;
        Cell cellStt = ExcelUtil.createCell(row, col++);
        cellStt.setCellValue(row.getRowNum() - 8);
        cellStt.setCellStyle(numberStyle);

        ExcelUtil.createCell(row, col++).setCellValue(dto.getLeeId() == null ? "" : dto.getLeeId().toString());

        Cell cellBusinessId = ExcelUtil.createCell(row, col++);
        cellBusinessId.setCellStyle(textStyle);
        cellBusinessId.setCellValue(Objects.nonNull(dto.getBusinessId()) ? dto.getBusinessId().toString() : dto.getBusinessIdCol());

        Cell cellDefaultValue = ExcelUtil.createCell(row, col++);
        cellDefaultValue.setCellStyle(textStyle);
        cellDefaultValue.setCellValue(Objects.nonNull(dto.getDefaultValue()) ? dto.getDefaultValue() : "");

        Cell cellLeeValue = ExcelUtil.createCell(row, col);
        cellLeeValue.setCellStyle(textStyle);
        cellLeeValue.setCellValue(Objects.nonNull(dto.getLeeValue()) ? dto.getLeeValue() : "");

//        Cell cellError = ExcelUtil.createCell(row, col);
//        cellError.setCellStyle(textStyleErr);
//        cellError.setCellValue(dto.getErrorsMess().size() == 0 ? "" : DataUtil.listStrToString(dto.getErrorsMess()));
    }
}
