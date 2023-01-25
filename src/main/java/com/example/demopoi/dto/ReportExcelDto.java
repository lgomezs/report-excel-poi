package com.example.demopoi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class ReportExcelDto implements Serializable {

    private static final long serialVersionUID = 6254347345685870036L;

    private InputStream inputStreamTemplate;

    private String fileName;

    private String sheetName;

    private String templateName;

    private String templateBasePath;

    private Integer startPositionHeader;

    private List<String> headerValues;

    private Map<Integer, Integer> headerColumnWidth = new HashMap<>();

    private List<ParameterDTO> parameters;

    private boolean isRequiredCellStyleHeader;

    private boolean isRequiredCellStyleBody;

}
