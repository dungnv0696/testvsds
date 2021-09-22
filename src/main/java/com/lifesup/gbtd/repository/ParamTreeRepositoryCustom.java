package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.ParamTreeDto;
import com.lifesup.gbtd.dto.object.ReportDto;
import com.lifesup.gbtd.dto.object.SheetDto;
import com.lifesup.gbtd.dto.object.TempReportDto;
import com.lifesup.gbtd.dto.response.ResponseCommon;

import java.util.List;

public interface ParamTreeRepositoryCustom {
    List<ParamTreeDto> getParent(ParamTreeDto obj);

    List<ParamTreeDto> getListUnit(ReportDto reportDto);

    List<ParamTreeDto> getListUnitDeptTree(ReportDto reportDto);

    List<ParamTreeDto> getListParamTree(ParamTreeDto obj);

    int createParamTree(ParamTreeDto obj);// transaction

    List<ParamTreeDto> getParamTreeByCode(ParamTreeDto obj);

    List<ParamTreeDto> getParamTreeById(ParamTreeDto obj);

    List<ParamTreeDto> getType();

    List<ParamTreeDto> getIdMax(String type);

    int editParamTree(ParamTreeDto obj);// transaction

    int editParamTreeChi(ParamTreeDto obj);// transaction

    ResponseCommon deleteParamTree(ParamTreeDto obj); // transaction

    //temp report
    List<TempReportDto> getListTempReport(String userName);

    List<TempReportDto> findTempReportByName(String reportName, String userName);

    int insertTempReport(TempReportDto tempReportDTO, String name); //trans

    int updateTempReport(TempReportDto tempReportDTO);

    int deleteTempReport(TempReportDto tempReportDTO);

    List<SheetDto> getSheet();
}
