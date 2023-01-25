package com.example.demopoi.component;

import com.example.demopoi.component.base.ReadFileExcelUtil;
import com.example.demopoi.dto.ParameterDTO;
import com.example.demopoi.dto.ReportExcelDto;
import com.example.demopoi.dto.UserExportData;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class UserExportReportComponent {
    private static final String SHEET_NAME = "Hoja1";

    private static final int INITIAL_TO_INSERT_ROW = 1;

    @Value("#{'${report.article.header.name.col}'.split(',')}")
    private List<String> headerListNameColumn;
    
    @Value("#{${report.article.header.width.col}}")
    private Map<Integer, Integer> headerWidthNameColumn;
    
    @Value("#{${report.article.header.name.col.position}}")
    private Map<String, Integer> mapHeaderName;

    public ReportExcelDto convertToReportExcelDTO(final List<UserExportData> articleEntityList,
                                                  final Locale locale) {
        final ReportExcelDto reportExcelDto = new ReportExcelDto();
        // data header
        reportExcelDto.setHeaderColumnWidth(this.headerWidthNameColumn);
        reportExcelDto.setStartPositionHeader(0);
        reportExcelDto.setHeaderValues(headerListNameColumn);
        reportExcelDto.setRequiredCellStyleHeader(true);
        // data body
        reportExcelDto.setRequiredCellStyleBody(true);
        final List<ParameterDTO> parameterList = this.buildParameter(articleEntityList);
        reportExcelDto.setParameters(parameterList);

        reportExcelDto.setSheetName(SHEET_NAME);
        return reportExcelDto;
    }

    private List<ParameterDTO> buildParameter(final List<UserExportData> articleEntityList) {
        log.info("[GENERATE_REPORT] buildParameter for report articles");
        final List<ParameterDTO> parameterList = new ArrayList<>();
        final AtomicInteger row = new AtomicInteger(INITIAL_TO_INSERT_ROW);

        articleEntityList.forEach(data -> {
            final Map<String, Object> mapParameters = new HashMap<>();
            mapParameters.put("firstName", data.getFirstName());
            mapParameters.put("lastName", this.getValueOfObject(data.getLastName()));
            mapParameters.put("code", this.getValueOfObject(data.getCode()));
            mapParameters.put("address",
                    this.getValueOfObject(data.getAddress()));
            mapParameters.put("email", this.getValueOfObject(data.getEmail()));
            
            parameterList.addAll(ReadFileExcelUtil.buildValueForDocument(this.mapHeaderName, mapParameters, row.get()));
            row.incrementAndGet();
        });

        return parameterList;
    }

    private Object getValueOfObject(final Object obj) {
        return obj != null ? obj : Strings.EMPTY;
    }

}
