package com.lifesup.gbtd.service.inteface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lifesup.gbtd.dto.object.ChartParamDto;
import com.lifesup.gbtd.dto.object.ChartResultDto;
import com.lifesup.gbtd.dto.object.ConfigChartDto;
import com.lifesup.gbtd.dto.object.SaveChartDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.lifesup.gbtd.dto.object.TableDto;

import java.text.ParseException;
import java.util.List;

public interface IConfigChartService {
    ChartResultDto buildChart(Long id, ChartParamDto params) throws ParseException, JsonProcessingException;

    Page<ConfigChartDto> doSearch(ConfigChartDto dto, Pageable pageable);

    List<TableDto> getDescriptionOfTableToMap(String tableName);

    // config chart
    ChartResultDto createConfigChart(SaveChartDto configChartDTO);

    ChartResultDto updateConfigChart(SaveChartDto configChartDTO);

    ChartResultDto previewChart(SaveChartDto configChartDTO) throws JsonProcessingException, ParseException;

    ConfigChartDto copy(Long id);

    void delete(Long id);

    void checkDelete(Long id);

    SaveChartDto getConfigChart(Long id);
}
