package com.lifesup.gbtd.service.inteface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lifesup.gbtd.dto.object.ActionAuditDto;
import com.lifesup.gbtd.dto.object.ConfigQueryChartDto;
import com.lifesup.gbtd.dto.object.SaveChartItemDto;
import com.lifesup.gbtd.dto.object.SaveDisplayQueryDto;

import java.util.List;
import java.util.Map;

public interface IBuildChartServiceTemp {
    List<SaveDisplayQueryDto> createDisplayQueries(List<SaveChartItemDto> items, String tableName);

    boolean isValidOperator(String operator);

    SaveChartItemDto generateInputCondition(SaveChartItemDto item, ConfigQueryChartDto query, List<SaveDisplayQueryDto> displayQueries) throws JsonProcessingException;

    void deleteItems(Long chartId, List<ActionAuditDto> actionLogs);

    Map<String, Object> createWhCls(List<SaveChartItemDto> items, String tableName);

    Map<String, Object> mergeParams(Map<String, Object> defaultParams, Map<String, Object> params);
}
