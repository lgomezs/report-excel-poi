package com.example.demopoi.component.base;

import com.example.demopoi.dto.ReportExcelDto;

import java.io.InputStream;

public interface GenerateReportExcelService {

    /**
     * Generate excel string.
     * @param reportExcel the report excel
     * @return the string
     */
    InputStream generateExcel(final ReportExcelDto reportExcel);

}
