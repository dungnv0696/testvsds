package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ParamTreeDto;
import com.lifesup.gbtd.dto.object.ReportDto;
import com.lifesup.gbtd.dto.object.SheetDto;
import com.lifesup.gbtd.dto.object.TempReportDto;
import com.lifesup.gbtd.dto.response.ResponseCommon;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Created by pmvt-os-chc-06 on 6/10/2020.
 */
public interface ITestReportService {

    ResponseCommon generateReport(ReportDto dataSearch);

    List<ParamTreeDto> getUnit(ReportDto reportDTO);

    List<ParamTreeDto> getUnitDeptTree(ReportDto reportDTO);

    List<TempReportDto> getTempReport(String userName);

    void updateTempReport(TempReportDto tempReportDTO);

    void insertTempReport(TempReportDto tempReportDTO, String name);

    void deleteTempReport(TempReportDto tempReportDTO);

    List<SheetDto> getSheet();

    ResponseEntity<Resource> downloadReport(String fileName);
}
