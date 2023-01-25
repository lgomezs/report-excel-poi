package com.example.demopoi;

import com.example.demopoi.component.UserExportReportComponent;
import com.example.demopoi.component.base.GenerateReportExcelService;
import com.example.demopoi.dto.ReportExcelDto;
import com.example.demopoi.dto.UserExportData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
@Slf4j
public class DemoPoiApplication {

	@Autowired
	private UserExportReportComponent userExportReportComponent;
	
	@Autowired
	private GenerateReportExcelService generateReportExcelService;
	
	
	public static void main(String[] args) {
		SpringApplication.run(DemoPoiApplication.class, args);		
	}

	@Bean
	public void doSomethingAfterStartup() throws IOException {
		log.info("start report");
		generateExcel();
		log.info("end report");
	}
	
	private void generateExcel() throws IOException {
		//generate data from BD
		final List<UserExportData> articleEntityList = new ArrayList<>();
		UserExportData userExportData = new UserExportData();
		userExportData.setAddress("jirn lima 2323");
		userExportData.setFirstName("miguel");
		userExportData.setLastName("gomez");
		userExportData.setCode(232231);
		userExportData.setEmail("luis@prueba.com");
		articleEntityList.add(userExportData);

		ReportExcelDto reportExcelDto =userExportReportComponent.convertToReportExcelDTO(articleEntityList, new Locale("ES"));

		final InputStream inputStream = this.generateReportExcelService.generateExcel(reportExcelDto);

		File file = new File("D:\\report.xlsx");
		FileUtils.copyInputStreamToFile(inputStream, file);
	}

}
