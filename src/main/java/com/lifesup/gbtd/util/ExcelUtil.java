package com.lifesup.gbtd.util;

import com.lifesup.gbtd.dto.object.ColumnProfile;
import com.lifesup.gbtd.dto.object.ExcelErrorDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@UtilityClass
public class ExcelUtil {

    public String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public boolean hasExcelFormat(MultipartFile file, Long fileSize) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return file.getSize() <= fileSize;
    }

    public CellStyle defaultStyleHeaderCell(XSSFWorkbook workbook, XSSFFont font) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        headerStyle.setFont(font);
        headerStyle.setWrapText(true);

        return headerStyle;
    }

    public CellStyle borderAll(CellStyle cellStyle) {
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);

        return cellStyle;
    }

    public CellStyle defaultStyleNumber(Workbook workbook) {
        CellStyle numberStyle = workbook.createCellStyle();
        borderAll(numberStyle);
        numberStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        numberStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);

        return numberStyle;
    }

    public CellStyle defaultStyleText(Workbook workbook) {
        CellStyle textStyle = workbook.createCellStyle();
        borderAll(textStyle);
        textStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        textStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);

        return textStyle;
    }

    public CellStyle defaultStyleTextErr(Workbook workbook) {
        CellStyle textStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(XSSFFont.COLOR_RED);
        textStyle.setFont(font);
        textStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        textStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);

        return textStyle;
    }

    public ExcelErrorDto createError(int row, String colAlias, String colName, String detail) {
        ExcelErrorDto err = new ExcelErrorDto();
        err.setColumnError(colAlias);
        err.setLineError(String.valueOf(row));
        err.setDetailError(colName + " " + detail);
        return err;
    }

    public ExcelErrorDto createError(int row, ColumnProfile colProp, String detail) {
        ExcelErrorDto err = new ExcelErrorDto();
        err.setColumnError(colProp.getAlias());
        err.setLineError(String.valueOf(row));
        err.setDetailError(colProp.getTitle() + " " + detail);
        return err;
    }

    public Cell createCell(Row row, int column) {
        return row.createCell(column);
    }

    public Cell createStyleCell(Row row, int column, CellStyle cellStyle) {
        Cell cell = createCell(row, column);
        cell.setCellStyle(cellStyle);
        return cell;
    }
}
