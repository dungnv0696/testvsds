package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.CatDepartmentDto;
import com.lifesup.gbtd.dto.object.ReportDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
import com.lifesup.gbtd.dto.object.TempReportDto;
import com.lifesup.gbtd.dto.object.UsersDto;
import com.lifesup.gbtd.dto.request.ServiceTargetResDto;
import com.lifesup.gbtd.model.TempReportEntity;

import java.util.HashMap;
import java.util.List;

public interface IDynamicReportService {
    HashMap<String, List> getCatItems();

    TempReportDto add(TempReportDto dto);

    void update(TempReportDto dto);

    void delete(TempReportDto dto);

    void shareReport(TempReportDto dto);

    List<TempReportDto> getListTempReport();

    List<ServiceGBTDDto> getListTargetService(ServiceGBTDDto dto);

    List<UsersDto> getListUsers();

    TempReportDto getTempReport(TempReportDto dto);

    List<CatDepartmentDto> getDepartmentTreeFromDeptId(CatDepartmentDto criteria);
}
