package com.lifesup.gbtd.dto.object;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class SaveChartItemDto extends ConfigChartItemDto {

    private String customizeSql;
    private String customizeMaxPrdIdSql;
    private Boolean joinCatGraphKpi;
    private List<SaveKpiInfoDto> kpiInfos;
    private List<SaveInputParamDto> params;
    private String limit;
    private List<SaveOrderByDto> orderBys;
    private List<SaveDisplayQueryDto> columns;
    private List<SaveDisplayQueryDto> outColumns;
    private List<Map<String, Object>> allColumns;

    public SaveChartItemDto() {
        params = new ArrayList<>();
        kpiInfos = new ArrayList<>();
        orderBys = new ArrayList<>();
        columns = new ArrayList<>();
        outColumns = new ArrayList<>();
    }

    public SaveChartItemDto(ConfigChartItemDto dto) throws JsonProcessingException {
        this.setId(dto.getId());
        this.setChartId(dto.getChartId());
        this.setTypeChart(dto.getTypeChart());
        this.setHasAvgLine(dto.getHasAvgLine());
        this.setListColor(dto.getListColor());
        this.setOrderIndex(dto.getOrderIndex());
        this.setQueryId(dto.getQueryId());
        this.setInputCondition(dto.getInputCondition());
        this.setStatus(dto.getStatus());
        this.setDescription(dto.getDescription());
        this.setUpdateTime(dto.getUpdateTime());
        this.setUpdateUser(dto.getUpdateUser());
        this.setDisplayConfigs(dto.getDisplayConfigs());

        if (StringUtils.isNotEmpty(dto.getInputCondition())) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            SaveInputConditionDto inputConditionObj = mapper.readValue(dto.getInputCondition(), SaveInputConditionDto.class);
            this.customizeSql = inputConditionObj.getCustomizeSql();
            this.kpiInfos = inputConditionObj.getKpiInfos();
            this.params = inputConditionObj.getParams();
            this.limit = inputConditionObj.getLimit();
            this.orderBys = inputConditionObj.getOrderBys();
            this.columns = inputConditionObj.getColumns();
            this.joinCatGraphKpi = inputConditionObj.getJoinCatGraphKpi();
        }
    }

    public ConfigChartItemDto toDto() throws JsonProcessingException {
        ConfigChartItemDto dto = new ConfigChartItemDto();
        dto.setId(this.getId());
        dto.setChartId(this.getChartId());
        dto.setDescription(this.getDescription());
        dto.setHasAvgLine(this.getHasAvgLine() == null ? 0 : this.getHasAvgLine());
        dto.setListColor(this.getListColor());
        dto.setOrderIndex(this.getOrderIndex());
        dto.setQueryId(this.getQueryId());
        dto.setStatus(this.getStatus());
        dto.setTypeChart(this.getTypeChart());
        dto.setUpdateTime(this.getUpdateTime());
        dto.setUpdateUser(this.getUpdateUser());
        SaveInputConditionDto inputConditionObj = new SaveInputConditionDto();
        inputConditionObj.setCustomizeSql(this.getCustomizeSql());
        inputConditionObj.setKpiInfos(this.getKpiInfos());
        inputConditionObj.setParams(this.getParams());
        inputConditionObj.setLimit(this.getLimit());
        inputConditionObj.setOrderBys(this.getOrderBys());
        inputConditionObj.setColumns(this.getColumns());
        inputConditionObj.setJoinCatGraphKpi(this.getJoinCatGraphKpi());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        dto.setInputCondition(mapper.writeValueAsString(inputConditionObj));
        return dto;
    }

    public boolean canMerge(SaveChartItemDto o) {
        if (this.equals(o)) {
            return true;
        }
        List<Long> thisKpiIds = this.kpiInfos.stream()
                .map(SaveKpiInfoDto::getKpis).flatMap(List::stream)
                .map(ServiceGBTDDto::getServiceId)
                .sorted()
                .collect(Collectors.toList());
        List<Long> thatKpiIds = o.getKpiInfos().stream()
                .map(SaveKpiInfoDto::getKpis).flatMap(List::stream)
                .map(ServiceGBTDDto::getServiceId)
                .sorted()
                .collect(Collectors.toList());
        if (StringUtils.isNotEmpty(this.customizeSql) && StringUtils.isNotEmpty(o.getCustomizeSql()))
            return this.getCustomizeSql().trim().equals(o.getCustomizeSql().trim());

        if (StringUtils.equals(this.limit, o.getLimit())
                && this.getJoinCatGraphKpi() == o.getJoinCatGraphKpi()
                && thisKpiIds.containsAll(thatKpiIds) && thisKpiIds.size() == thatKpiIds.size()
                && this.params.containsAll(o.getParams()) && this.params.size() == o.getParams().size())
            return this.orderBys.containsAll(o.getOrderBys()) && this.orderBys.size() == o.getOrderBys().size();
        return false;
    }
}

@Getter
@Setter
class SaveInputConditionDto {
    private String customizeSql;
    private Boolean joinCatGraphKpi;
    private List<SaveKpiInfoDto> kpiInfos;
    private List<SaveInputParamDto> params;
    private String limit;
    private List<SaveOrderByDto> orderBys;
    private List<SaveDisplayQueryDto> columns;
}
