
package com.example.demopoi.component.base;


import com.example.demopoi.dto.ParameterDTO;
import com.example.demopoi.exception.ErrorApplication;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
public class ReadFileExcelUtil {

    /**
     * Instantiates a new Read file excel util.
     */
    ReadFileExcelUtil() {
    }

    /**
     * Process file upload file response dto.
     * @param <T> the type parameter
     * @param multipartFile the multipart file
     * @param function the function
     * @param SheetAt the sheet at
     * @param startRowData the start row data
     * @param endColumnData the end column data
     * @param maxRowToRead the max row to read
     * @return the upload file response dto
     */
    @SuppressWarnings("rawtypes")
    public static <T> UploadFileResponseDto<T> processFile(final MultipartFile multipartFile,
            final Function<Object, T> function, final int SheetAt, final int startRowData, final int endColumnData,
            final long maxRowToRead) {
        final UploadFileResponseDto<T> result = new UploadFileResponseDto(multipartFile.getOriginalFilename());
        final List<T> entityList = new ArrayList<>();
        final Workbook wb = getWorkbook(multipartFile);
        final Sheet sheet = wb.getSheetAt(SheetAt);
        int rowsCount = 1;

        for (int cantRows = startRowData; cantRows < sheet.getLastRowNum() + 1; cantRows++) {
            final T entity;
            final List<ParameterDTO> dataExcel = new ArrayList<>();
            final Row row = sheet.getRow(cantRows);
            if (row != null) {
                for (int colNum = 0; colNum < endColumnData; colNum++) {
                    final Cell cell = row.getCell(colNum, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    final ParameterDTO parameterDTO = new ParameterDTO();
                    parameterDTO.setColumn(cell.getColumnIndex());
                    parameterDTO.setValue(getValueData(cell));
                    dataExcel.add(parameterDTO);
                }
            }

            if (!dataExcel.stream().allMatch(s -> s.getValue().equals(Strings.EMPTY))) {
                entity = function.apply(dataExcel);
                if (entity != null) {
                    entityList.add(entity);
                }

                validateMaxRowToRead(rowsCount, maxRowToRead);
                rowsCount++;
            }
        }

        closeWorkbook(wb);
        result.setTotal((long) entityList.size());
        result.setEntityList(entityList);

        return result;
    }

    private static Workbook getWorkbook(final MultipartFile multipartFile) {
        try {
            return WorkbookFactory.create(multipartFile.getInputStream());
        } catch (final Exception exception) {
            log.error("Error getWorkbook {} :", exception.getMessage());
            throw new ErrorApplication(INTERNAL_SERVER_ERROR.value(),
                    "Error getWorkbook",
                    exception.getMessage());
        }
    }

    private static void closeWorkbook(final Workbook wb) {
        try {
            wb.close();
        } catch (final Exception exception) {
            log.error("Error closeWorkbook {} :", exception.getMessage());
            throw new ErrorApplication(INTERNAL_SERVER_ERROR.value(),
                    "Error closeWorkbook",
                    exception.getMessage());
        }
    }

    private static void validateMaxRowToRead(final int rowsCount, final long maxRowToRead) {
        log.debug("validateMaxRowToRead  rows find {} , maxRowToRead {}  ", rowsCount, maxRowToRead);
        if (rowsCount > maxRowToRead) {
            throw new ErrorApplication(BAD_REQUEST.value(),
                    "Error validateMaxRowToRead",
                    "error validateMaxRowToRead");
        }
    }


    private static String getValueData(final Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue().trim();
            default:
                return Strings.EMPTY;
        }
    }

    /**
     * Build document parameter parameter dto.
     * @param row the row
     * @param column the column
     * @param value the value
     * @return the parameter dto
     */
    public static ParameterDTO buildDocumentParameter(final Integer row, final Integer column, final Object value) {
        final ParameterDTO documentParameterDto = new ParameterDTO();
        documentParameterDto.setValue(value);
        documentParameterDto.setRow(row);
        documentParameterDto.setColumn(column);
        return documentParameterDto;
    }

    /**
     * Build value for document list.
     * @param mapHeaderName the map header name
     * @param parameters the parameters
     * @param row the row
     * @return the list
     */
    public static List<ParameterDTO> buildValueForDocument(final Map<String, Integer> mapHeaderName,
            final Map<String, Object> parameters, final Integer row) {
        final List<ParameterDTO> documentParameters;
        final List<String> cellValues = new ArrayList<>(mapHeaderName.keySet());
        documentParameters = cellValues.stream()
            .filter(parameters::containsKey)
            .map(cellValue -> ReadFileExcelUtil.buildDocumentParameter(row, mapHeaderName.get(cellValue),
                    parameters.get(cellValue)))
            .collect(Collectors.toList());

        return documentParameters;
    }

    /**
     * The type Upload file response dto.
     *
     * @param <E> the type parameter
     */
    @Getter
    @Setter
    public static class UploadFileResponseDto<E> {

        private String fileName;

        private Long total;

        /**
         * The Entity list.
         */
        List<E> entityList;

        /**
         * Instantiates a new Upload file response dto.
         * @param fileName the file name
         */
        public UploadFileResponseDto(final String fileName) {
            this.fileName = fileName;
            this.total = 0L;
        }

    }

}
