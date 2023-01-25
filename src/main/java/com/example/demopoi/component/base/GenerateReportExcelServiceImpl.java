package com.example.demopoi.component.base;

import com.example.demopoi.dto.ParameterDTO;
import com.example.demopoi.dto.ReportExcelDto;
import com.example.demopoi.exception.ErrorApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@Service
@Slf4j
public class GenerateReportExcelServiceImpl implements GenerateReportExcelService {

    @Override
    public InputStream generateExcel(final ReportExcelDto reportExcel) {
        try {
            log.info("start generate excel");
            final SXSSFWorkbook workbook = createWorkBook(reportExcel.getSheetName());

            final CellStyle xssfCellStyle = this.getStyleHeaderTabla(workbook.createFont(),
                    workbook.createCellStyle());
            final CellStyle xssfCellStyleBody = this.getStyleBodyTabla(workbook.createFont(),
                    workbook.createCellStyle());

            this.addHeader(workbook.getSheet(reportExcel.getSheetName()), xssfCellStyle,
                    reportExcel.getStartPositionHeader(),
                    reportExcel.getHeaderValues(),
                    reportExcel.getHeaderColumnWidth(), reportExcel.isRequiredCellStyleHeader());

            this.addBody(workbook.getSheet(reportExcel.getSheetName()), xssfCellStyleBody,
                    reportExcel);

            log.info("End generate excel");

            return this.writeWorkbookToByteArray(workbook);

        } catch (final Exception exception) {
            log.error("Error generateExcel {}  ", exception.getMessage());
            throw new ErrorApplication(INTERNAL_SERVER_ERROR.value(),
                    "Error generateExcel",
                    exception.getMessage());
        }
    }

    private static SXSSFWorkbook createWorkBook(final String nameSheet) {
        final SXSSFWorkbook workbook = new SXSSFWorkbook(10000);
        final SXSSFSheet sheet = workbook.createSheet(nameSheet);
        sheet.trackAllColumnsForAutoSizing();
        sheet.setRandomAccessWindowSize(10000);
        return workbook;
    }

    private void addHeader(final SXSSFSheet sheet, final CellStyle xssfCellStyle, Integer startPosition,
            final List<String> headerValues,
            final Map<Integer, Integer> headerColumnWidth, final boolean isRequiredCellStyleHeader) {
        // Se agrega ancho a las columnas
        headerColumnWidth.forEach(sheet::setColumnWidth);
        // Se agrega los datos de los headers
        final SXSSFRow row = sheet.createRow(startPosition);

        for (final String header : headerValues) {
            final SXSSFCell cell = row.createCell(startPosition);
            cell.setCellValue(header);
            if (isRequiredCellStyleHeader) {
                cell.setCellStyle(xssfCellStyle);
            }
            startPosition++;
        }
    }

    private void addBody(final SXSSFSheet sheet, final CellStyle xssfCellStyleBody, final ReportExcelDto reportExcel) {
        final List<ParameterDTO> parameters = reportExcel.getParameters() != null ? reportExcel.getParameters()
                : Collections.emptyList();
        for (final ParameterDTO parameter : parameters) {
            SXSSFRow row = sheet.getRow(parameter.getRow());
            if (row == null) {
                row = sheet.createRow(parameter.getRow());
            }

            final SXSSFCell cell = row.createCell(parameter.getColumn());
            this.addCellValue(cell, parameter.getValue());

            if (reportExcel.isRequiredCellStyleBody()) {
                cell.setCellStyle(xssfCellStyleBody);
            }
        }
    }

    private void addCellValue(final SXSSFCell cell, final Object value) {
        if (value instanceof String) {
            cell.setCellValue(String.valueOf(value));
        } else if (value instanceof Double) {
            cell.setCellValue(Double.parseDouble(String.valueOf(value)));
        } else if (value instanceof Integer) {
            cell.setCellValue(Integer.parseInt(String.valueOf(value)));
        } else {
            cell.setCellValue(String.valueOf(value));
        }
    }


    private ByteArrayInputStream writeWorkbookToByteArray(final SXSSFWorkbook workbook) throws Exception {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            workbook.close();
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (final IOException e) {
            throw new Exception("Error writing export Workbook", e);
        }
    }

    private CellStyle getStyleBodyTabla(final Font fontFilterParameter,
            final CellStyle styleFilterParameter) {
        fontFilterParameter.setFontHeightInPoints((short) 9);
        fontFilterParameter.setBold(true);
        styleFilterParameter.setFont(fontFilterParameter);
        return styleFilterParameter;
    }

    private CellStyle getStyleHeaderTabla(final Font fontExcel, final CellStyle cellStyle) {
        return this.generateStyle(fontExcel, cellStyle, 10, true, IndexedColors.WHITE.getIndex(),
                HorizontalAlignment.CENTER,
                VerticalAlignment.TOP, BorderStyle.THIN, IndexedColors.SEA_GREEN.getIndex());
    }

    private CellStyle generateStyle(
            final Font fontExcel, final CellStyle cellStyle, final int sizeFont, final boolean styleFont,
            final short fontColor,
            final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment,
            final BorderStyle borderStyle, final short backgroundColor) {

        fontExcel.setFontHeightInPoints((short) sizeFont);
        fontExcel.setBold(styleFont);
        fontExcel.setColor(fontColor);

        cellStyle.setFont(fontExcel);
        cellStyle.setAlignment(horizontalAlignment);
        cellStyle.setVerticalAlignment(verticalAlignment);
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setWrapText(true);
        cellStyle.setFillForegroundColor(backgroundColor);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

}
