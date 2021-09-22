package com.lifesup.gbtd.service.inteface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lifesup.gbtd.dto.object.ActionAuditDto;
import com.lifesup.gbtd.dto.object.ChartParamDto;
import com.lifesup.gbtd.dto.object.ChartResultDto;
import com.lifesup.gbtd.dto.object.ConfigChartDto;
import com.lifesup.gbtd.dto.object.ConfigChartItemDto;
import com.lifesup.gbtd.dto.object.ConfigQueryChartDto;
import com.lifesup.gbtd.dto.object.SaveChartDto;
import com.lifesup.gbtd.dto.object.SaveChartItemDto;
import com.lifesup.gbtd.dto.object.SaveDisplayQueryDto;
import com.lifesup.gbtd.dto.object.SaveOrderByDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDChartDto;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface IBuildChartService {
    ChartResultDto getChartResult(ConfigChartDto chartDto, List<ConfigChartItemDto> chartItems, ChartParamDto filterParamsObj) throws ParseException, JsonProcessingException;

    Map<String, String> getParamDefault(Map<String, Object> params);

    List<ServiceGBTDChartDto> getKpiInfoForItemOfResultChart(SaveChartItemDto saveItem, Object firstRecord, Long unitIdView);

    String processChartTitle(ChartResultDto chart);

    ChartResultDto saveChart(SaveChartDto dto);

    Map<SaveChartItemDto, List<SaveChartItemDto>> mergeQueries(SaveChartDto dto);

    ConfigQueryChartDto buildQuery(List<SaveChartItemDto> items, boolean hasSave, List<ActionAuditDto> actionLogs);

    String createOrderBy(List<SaveOrderByDto> orderBys, String tableName, boolean overview);
}
