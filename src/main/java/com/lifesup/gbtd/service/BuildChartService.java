package com.lifesup.gbtd.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.ActionAuditDto;
import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.dto.object.ChartDetailDto;
import com.lifesup.gbtd.dto.object.ChartParamDto;
import com.lifesup.gbtd.dto.object.ChartResultDto;
import com.lifesup.gbtd.dto.object.ConfigChartDto;
import com.lifesup.gbtd.dto.object.ConfigChartItemDto;
import com.lifesup.gbtd.dto.object.ConfigDisplayQueryDto;
import com.lifesup.gbtd.dto.object.ConfigQueryChartDto;
import com.lifesup.gbtd.dto.object.SaveChartDto;
import com.lifesup.gbtd.dto.object.SaveChartItemDto;
import com.lifesup.gbtd.dto.object.SaveDisplayQueryDto;
import com.lifesup.gbtd.dto.object.SaveKpiInfoDto;
import com.lifesup.gbtd.dto.object.SaveOrderByDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDChartDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.CatDepartmentEntity;
import com.lifesup.gbtd.model.ConfigChartEntity;
import com.lifesup.gbtd.model.ConfigChartItemEntity;
import com.lifesup.gbtd.model.ConfigDisplayQueryEntity;
import com.lifesup.gbtd.model.ConfigQueryChartEntity;
import com.lifesup.gbtd.repository.CatDepartmentRepository;
import com.lifesup.gbtd.repository.ConfigChartItemRepository;
import com.lifesup.gbtd.repository.ConfigChartRepository;
import com.lifesup.gbtd.repository.ConfigChartRoleRepository;
import com.lifesup.gbtd.repository.ConfigDisplayQueryRepository;
import com.lifesup.gbtd.repository.ConfigQueryChartRepository;
import com.lifesup.gbtd.repository.ExecuteSqlRepository;
import com.lifesup.gbtd.service.inteface.IBuildChartService;
import com.lifesup.gbtd.service.inteface.IBuildChartServiceTemp;
import com.lifesup.gbtd.service.inteface.ICatItemService;
import com.lifesup.gbtd.service.inteface.IServiceGBTDService;
import com.lifesup.gbtd.service.inteface.ISqlParserService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JsonUtil;
import com.lifesup.gbtd.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.lifesup.gbtd.util.Const.PARAM_CHART_DEFAULT.*;


@Service
@Slf4j
public class BuildChartService extends BaseService implements IBuildChartService {

    private static final int NGAY = 1;
    private static final int THANG = 2;
    private static final int QUY = 3;
    private static final int NAM = 4;
    private final Set<String> rptDataNewestField
            = new HashSet<>(Arrays.asList("SERVICE_ID", "INPUT_LEVEL", "TIME_TYPE", "PRD_ID", "OBJ_CODE"));

    private final IServiceGBTDService serviceGBTDService;
    private final ExecuteSqlRepository executeSqlRepository;
    private final ICatItemService catItemService;
    private final ConfigChartRepository configChartRepository;
    private final ConfigChartItemRepository configChartItemRepository;
    private final ConfigDisplayQueryRepository configDisplayQueryRepository;
    private final ConfigQueryChartRepository configQueryChartRepository;
    private final ISqlParserService sqlParserService;
    private final ConfigChartRoleRepository configChartRoleRepository;
    private final CatDepartmentRepository catDepartmentRepository;
    private final IBuildChartServiceTemp iBuildChartServiceTemp;

    @Autowired
    public BuildChartService(
            IServiceGBTDService serviceGBTDService,
            ExecuteSqlRepository executeSqlRepository,
            ICatItemService catItemService,
            ConfigChartRepository configChartRepository,
            ConfigChartItemRepository configChartItemRepository,
            ConfigDisplayQueryRepository configDisplayQueryRepository,
            ConfigQueryChartRepository configQueryChartRepository,
            ISqlParserService sqlParserService,
            ConfigChartRoleRepository configChartRoleRepository,
            CatDepartmentRepository catDepartmentRepository,
            IBuildChartServiceTemp iBuildChartServiceTemp) {
        this.serviceGBTDService = serviceGBTDService;
        this.executeSqlRepository = executeSqlRepository;
        this.catItemService = catItemService;
        this.configChartRepository = configChartRepository;
        this.configChartItemRepository = configChartItemRepository;
        this.configDisplayQueryRepository = configDisplayQueryRepository;
        this.configQueryChartRepository = configQueryChartRepository;
        this.sqlParserService = sqlParserService;
        this.configChartRoleRepository = configChartRoleRepository;
        this.catDepartmentRepository = catDepartmentRepository;
        this.iBuildChartServiceTemp = iBuildChartServiceTemp;
    }

    @Override
    public ChartResultDto getChartResult(ConfigChartDto chartDto, List<ConfigChartItemDto> chartItems, ChartParamDto filterParamsObj) throws ParseException, JsonProcessingException {
        List<Long> unitUsed = new ArrayList<>();
        ChartResultDto result = new ChartResultDto(chartDto);
        if (DataUtil.isNullOrEmpty(chartItems)) return result;
        Map<ConfigQueryChartDto, List<ConfigChartItemDto>> mergeQueries = chartItems.stream().filter(i -> i.getQuery() != null).collect(Collectors.groupingBy(ConfigChartItemDto::getQuery, Collectors.mapping((ConfigChartItemDto i) -> i, Collectors.toList())));
        List<ChartDetailDto> details = new ArrayList<>();
        if (!DataUtil.isNullOrEmpty(mergeQueries)) {
            for (Map.Entry<ConfigQueryChartDto, List<ConfigChartItemDto>> entry : mergeQueries.entrySet()) {
                Map<String, Object> params = filterParamsObj != null && !DataUtil.isNullOrEmpty(filterParamsObj.toMap()) ? filterParamsObj.toMap() : null;
                ConfigQueryChartDto query = entry.getKey();
                SaveChartItemDto item = new SaveChartItemDto(entry.getValue().get(0));
                Map<String, Object> defaultParams = JsonUtil.toMap(query.getDefaultValue());
                for (Map.Entry<String, Object> map : defaultParams.entrySet()) {
                    if (map.getValue() instanceof String && ((String) map.getValue()).indexOf(",") > -1) {
                        defaultParams.put(map.getKey(), new ArrayList<String>(Arrays.asList(((String) map.getValue()).split("\\,"))));
                    }
                }
                defaultParams = iBuildChartServiceTemp.mergeParams(defaultParams, params);
                if (DataUtil.isNullOrEmpty(defaultParams)) {
                    defaultParams = new HashMap<>();
                }
//                if (Const.TYPE_CHART.BLOCK_INFO_2.equals(chartDto.getTypeChart())) {
                    List<SaveDisplayQueryDto> displayQueryDtos = item.getColumns().stream().filter(itemValue -> Const.CAT_ITEM_CODE.TIME_TYPE.equals(itemValue.getColumnChart())).collect(Collectors.toList());
                    if (displayQueryDtos.size() == 1 && displayQueryDtos.get(0).getValues().size() == 1) {
                        String timeType = displayQueryDtos.get(0).getValues().get(0).getValue();
                        if (Const.TIME_TYPE.STR_DATE.equals(timeType)) {
                            defaultParams.put(Const.FILTER_PARAMS.TABLE_NAME, Const.TABLE.RPT_GRAPH_DAY);
                            defaultParams.put(Const.FILTER_PARAMS.TIME_TYPE_PARAM, timeType);
                        }
                        if (Const.TIME_TYPE.STR_MONTH.equals(timeType)) {
                            defaultParams.put(Const.FILTER_PARAMS.TABLE_NAME, Const.TABLE.RPT_GRAPH_MON);
                            defaultParams.put(Const.FILTER_PARAMS.TIME_TYPE_PARAM, timeType);
                        }
                        if (Const.TIME_TYPE.STR_QUARTER.equals(timeType)) {
                            defaultParams.put(Const.FILTER_PARAMS.TABLE_NAME, Const.TABLE.RPT_GRAPH_QUAR);
                            defaultParams.put(Const.FILTER_PARAMS.TIME_TYPE_PARAM, timeType);
                        }
                        if (Const.TIME_TYPE.STR_YEAR.equals(timeType)) {
                            defaultParams.put(Const.FILTER_PARAMS.TABLE_NAME, Const.TABLE.RPT_GRAPH_YEAR);
                            defaultParams.put(Const.FILTER_PARAMS.TIME_TYPE_PARAM, timeType);
                        }
                    }
//                }
                if (!defaultParams.containsKey(Const.FILTER_PARAMS.KPI_IDS_PARAM)) {
                    if (!DataUtil.isNullOrEmpty(item.getKpiInfos())) {
                        List<Long> kpiIds = item.getKpiInfos().stream().map(ki -> {
                            if (!DataUtil.isNullOrEmpty(ki.getKpis())) {
                                return ki.getKpis().stream().map(ServiceGBTDDto::getServiceId).collect(Collectors.toList());
                            }
                            return new ArrayList<Long>();
                        }).flatMap(List::stream).collect(Collectors.toList());
                        defaultParams.put(Const.FILTER_PARAMS.KPI_IDS_PARAM, kpiIds);
                    }
                }
                caclTimeFilter(defaultParams, chartDto, query);
                query.setParams(defaultParams);
                try {
                    query.setQueryData(query.getQueryData().replace("data.SERVICE_ID DATA_SERVICE_ID", "data.SERVICE_ID DATA_SERVICE_ID, DATA.OBJ_CODE").replace("SELECT DATA_SERVICE_ID", "SELECT DATA_SERVICE_ID, OBJ_CODE"));
                    List<Object> data = executeSqlRepository.executeSql(query.getQueryData(), defaultParams);
                    query.setData(data);
                    log.info(query.getQueryData());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new ServerException(ErrorCode.NOT_VALID, Const.TABLE.CONFIG_CHART);
                }
                if (!DataUtil.isNullOrEmpty(query.getParams())) result.setFilterParams(query.getParams());
            }
        }
        this.setServiceForDetail(details, mergeQueries, result, unitUsed, filterParamsObj);
        chartDto.setUnitUsedIds(unitUsed);
        result.setDetails(details);
        result.setTitleChart(processChartTitle(result));
        if (!DataUtil.isNullObject(chartDto.getDeptIdService())) {
            CatDepartmentEntity catDepartmentEntity = catDepartmentRepository.findById(chartDto.getDeptIdService()).get();
            if (!DataUtil.isNullObject(catDepartmentEntity)) {
                result.setDeptCodeService(catDepartmentEntity.getCode());
            }
        }
        return result;
    }

    private void setServiceForDetail(List<ChartDetailDto> details,
                                     Map<ConfigQueryChartDto, List<ConfigChartItemDto>> mergeQueries,
                                     ChartResultDto result,
                                     List<Long> unitUsed, ChartParamDto param) throws JsonProcessingException {
        for (Map.Entry<ConfigQueryChartDto, List<ConfigChartItemDto>> entry : mergeQueries.entrySet()) {
            for (ConfigChartItemDto chartItem : entry.getValue()) {
                ChartDetailDto detail = new ChartDetailDto();
                SaveChartItemDto saveItem = new SaveChartItemDto(chartItem);
                detail.setId(chartItem.getId());
                detail.setChartType(chartItem.getTypeChart());
                detail.setOrderIndex(chartItem.getOrderIndex());
                detail.setData(entry.getKey().getData());
                detail.setDisplayConfigs(chartItem.getDisplayConfigs());
                detail.setQuery(chartItem.getQuery());
                if (null != param && !DataUtil.isNullObject(param.getKpiIds())) {
                    for (int i = 0; i < param.getKpiIds().size(); i++) {
                        if (i > saveItem.getKpiInfos().get(0).getKpis().size() - 1) {
                            saveItem.getKpiInfos().get(0).getKpis().add(new ServiceGBTDDto());
                        }
                        List<ServiceGBTDDto> serviceGBTDDtos = this.serviceGBTDService.findByServiceId(Long.valueOf(param.getKpiIds().get(i)));
                        saveItem.getKpiInfos().get(0).getKpis().get(i).setServiceId(serviceGBTDDtos.get(0).getServiceId());
                        saveItem.getKpiInfos().get(0).getKpis().get(i).setServiceName(serviceGBTDDtos.get(0).getServiceName());
                    }

                }

                List<ServiceGBTDChartDto> kpis = this.getKpiInfoForItemOfResultChart(
                        saveItem,
                        DataUtil.isNullOrEmpty(detail.getData()) ? null : detail.getData().get(0),
                        result.getUnitIdView());
                if (!DataUtil.isNullOrEmpty(kpis)) {
                    detail.setKpiInfo(kpis.get(0));
                    detail.setKpiInfos(kpis);
                    unitUsed.add(kpis.get(0).getUnitId());
                }
                details.add(detail);
            }
        }
        details.sort((a, b) -> {
            if (a.getOrderIndex() == null && b.getOrderIndex() != null) return 1;
            if (a.getOrderIndex() != null && b.getOrderIndex() == null) return -1;
            if (a.getOrderIndex() == null && b.getOrderIndex() == null) return 0;
            return a.getOrderIndex().compareTo(b.getOrderIndex());
        });
    }

    public void caclTimeFilter(Map<String, Object> params, ConfigChartDto chart, ConfigQueryChartDto queryChart) throws ParseException, JsonProcessingException, ParseException {
        Map<String, Object> defaultParamsTemp = JsonUtil.toMap(queryChart.getDefaultValue());
        Map<String, Object> defaultParams = new HashMap<>();
        for (String key : defaultParamsTemp.keySet()) {
            defaultParams.put(key.toUpperCase(), defaultParamsTemp.get(key));
        }
        Long timeType = chart.getTimeType();
        if (params.containsKey(Const.FILTER_PARAMS.TIME_TYPE_PARAM)) {
            timeType = DataUtil.safeToLong(params.get(Const.FILTER_PARAMS.TIME_TYPE_PARAM));
        }
        Map<String, String> paramDefault = this.getParamDefault(params);
        Integer currentDate = DataUtil.getDateInt(new Date(), Const.DATE_FORMAT_YYYYMMDD);

        if (currentDate == null) {
            currentDate = DataUtil.getDateInt(new Date(), Const.DATE_FORMAT_YYYYMMDD);
        }
        Integer maxPrdId = currentDate;
        Integer toDate = DataUtil.safeToInt(params.get(Const.FILTER_PARAMS.TO_DATE_PARAM));
        if (DataUtil.isDate(toDate.toString(), Const.DATE_FORMAT_YYYYMMDD)) {
            maxPrdId = toDate;
            currentDate = toDate;
        } else if (StringUtils.isNotEmpty(queryChart.getQueryMaxPrdId())) {
            try {
                maxPrdId = executeSqlRepository.getMaxPrdId(queryChart.getQueryMaxPrdId(), params);
                maxPrdId = maxPrdId == null ? currentDate : maxPrdId;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ServerException(ErrorCode.NOT_VALID, "query max prd id");
            }
        }
        boolean isRelativeTime = false;
        for (Map.Entry<String, String> entry : paramDefault.entrySet()) {
            if (Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME.equals(entry.getValue())) {
                isRelativeTime = true;
                Integer date = DataUtil.getAbsoluteDate(currentDate, chart.getRelativeTime() == null ? 0 : chart.getRelativeTime().intValue(),
                        timeType);
                params.put(entry.getKey(), String.valueOf(date));

            }
        }

        for (Map.Entry<String, String> entry : paramDefault.entrySet()) {
            if (entry.getValue().equals(Const.PARAM_CHART_DEFAULT.MAX_DATE)) {
                params.put(entry.getKey(), String.valueOf(maxPrdId));
            }
            if (entry.getValue().equals(Const.PARAM_CHART_DEFAULT.MAX_DATE_NTIME)) {
                params.put(entry.getKey(), handleTimeToDate(executeSqlRepository.getMaxPrdId(queryChart.getQueryMaxPrdId(), params), chart.getRelativeTime().intValue(), chart.getTimeType().intValue()));
            }
            if (entry.getValue().equals(BEGIN_YEAR)) {
                if (isRelativeTime) {
                    Integer date = DataUtil.getAbsoluteDate(currentDate, chart.getRelativeTime() == null ? 0 : chart.getRelativeTime().intValue(),
                            timeType);
                    params.put(entry.getKey(), String.valueOf(getDate(BEGIN_YEAR, chart, date)));
                } else {
                    params.put(entry.getKey(), String.valueOf(getDate(BEGIN_YEAR, chart, maxPrdId.intValue())));
                }
            }
            if (entry.getValue().equals(Const.PARAM_CHART_DEFAULT.END_YEAR)) {
                params.put(entry.getKey(), String.valueOf(getDate(END_YEAR, chart, maxPrdId.intValue())));
            }
            if (entry.getValue().equals(Const.PARAM_CHART_DEFAULT.BEGIN_MONTH)) {
                if (isRelativeTime) {
                    Integer date = DataUtil.getAbsoluteDate(currentDate, chart.getRelativeTime() == null ? 0 : chart.getRelativeTime().intValue(),
                            timeType);
                    params.put(entry.getKey(), String.valueOf(getDate(BEGIN_MONTH, chart, date)));
                } else {
                    params.put(entry.getKey(), String.valueOf(getDate(BEGIN_MONTH, chart, maxPrdId.intValue())));
                }
            }
            if (entry.getValue().equals(Const.PARAM_CHART_DEFAULT.END_MONTH)) {
                params.put(entry.getKey(), String.valueOf(getDate(END_MONTH, chart, maxPrdId.intValue())));
            }
            if (entry.getValue().equals(Const.PARAM_CHART_DEFAULT.BEGIN_QUAR)) {
                if (isRelativeTime) {
                    Integer date = DataUtil.getAbsoluteDate(currentDate, chart.getRelativeTime() == null ? 0 : chart.getRelativeTime().intValue(),
                            timeType);
                    params.put(entry.getKey(), String.valueOf(getDate(BEGIN_QUAR, chart, date)));
                } else {
                    params.put(entry.getKey(), getDate(BEGIN_QUAR, chart, maxPrdId.intValue()));
                }
            }
            if (entry.getValue().equals(Const.PARAM_CHART_DEFAULT.END_QUAR)) {
                params.put(entry.getKey(), getDate(END_QUAR, chart, executeSqlRepository.getMaxPrdId(queryChart.getQueryMaxPrdId(), params)));
            }
            if (Arrays.asList(Const.PARAM_CHART_DEFAULT.MAX_DATE_NDATE, Const.PARAM_CHART_DEFAULT.MAX_DATE_NMONTH,
                    Const.PARAM_CHART_DEFAULT.MAX_DATE_NQUAR, Const.PARAM_CHART_DEFAULT.MAX_DATE_NYEAR)
                    .contains(entry.getValue())) {
                int rangeTime = getRangeTimeFromMaxDate(entry.getValue());
                Integer date = DataUtil.getAbsoluteDate(maxPrdId, rangeTime, timeType);
                params.put(entry.getKey(), String.valueOf(date));
            }
            if (Arrays.asList(Const.PARAM_CHART_DEFAULT.MAX_DATE_NDAY).contains(entry.getValue())) {
                Integer date = DataUtil.getAbsoluteDate(maxPrdId, chart.getRelativeTime() == null ? 0 : chart.getRelativeTime().intValue(), timeType);
                params.put(entry.getKey(), String.valueOf(date));
            }
            if (Const.PARAM_CHART_DEFAULT.CURRENT_DATE.equals(entry.getValue())) {
                params.put(entry.getKey(), String.valueOf(currentDate));
            }
            if (Arrays.asList(Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NDATE, Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NMONTH,
                    Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NQUAR, Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NYEAR)
                    .contains(entry.getValue())) {
                Integer date = DataUtil.getAbsoluteDate(currentDate,
                        chart.getRelativeTime() == null ? 0 : chart.getRelativeTime().intValue(),
                        timeType);
                int rangeTime = getRangeTimeFromMaxDate(entry.getValue());
                date = DataUtil.getAbsoluteDate(date, rangeTime, timeType);
                params.put(entry.getKey(), String.valueOf(date));
            }
        }
        this.handleFromDateParamsValue(params, defaultParams, toDate, currentDate, timeType);
    }

    private String handleTimeToDate(Integer maxPrdId, int relativeTime, int timeType) {
        LocalDate date = LocalDate.parse(String.valueOf(maxPrdId), DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
        switch (timeType) {
            case 1:
                if (relativeTime > 0) {
                    date = date.plusDays(relativeTime);
                    return date.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                }
                if (relativeTime < 0) {
                    date = date.minusDays(Math.abs(relativeTime));
                    return date.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                }
                break;
            case 2:
                if (relativeTime > 0) {
                    date = date.plusMonths(relativeTime);
                    return date.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                }
                if (relativeTime < 0) {
                    date = date.minusMonths(Math.abs(relativeTime));
                    return date.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                }
                break;
            case 3:
                if (relativeTime > 0) {
                    date = date.plusMonths(3 * relativeTime);
                    return date.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                }
                if (relativeTime < 0) {
                    date = date.minusMonths(3 * Math.abs(relativeTime));
                    return date.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                }
                break;
            case 4:
                if (relativeTime > 0) {
                    date = date.plusYears(relativeTime);
                    return date.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                }
                if (relativeTime < 0) {
                    date = date.minusYears(Math.abs(relativeTime));
                    return date.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                }
                break;
        }


        return date.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
    }

    private String getDate(String value, ConfigChartDto chart, int maxPrd) {
        String date = "";
        LocalDate now = LocalDate.now();
        LocalDate maxPrdDate = LocalDate.parse(String.valueOf(maxPrd), DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
        now = maxPrdDate;
        LocalDate firstDayOfQuarter = now.with(now.getMonth().firstMonthOfQuarter())
                .with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfQuarter = firstDayOfQuarter.plusMonths(2)
                .with(TemporalAdjusters.lastDayOfMonth());
        switch (value) {
            case BEGIN_YEAR:
                date = now.format(DateTimeFormatter.ofPattern("YYYY")) + "0101";
                now = now.with(TemporalAdjusters.firstDayOfYear());
                if (chart.getRelativeTime() != null && chart.getTimeType() == NGAY) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue());
                }
                if (chart.getRelativeTime() != null && chart.getTimeType() == THANG) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue()).substring(0, 6) + "01";
                }
                if (chart.getRelativeTime() != null && chart.getTimeType() == QUY) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue()).substring(0, 6) + "01";
                }
                if (chart.getRelativeTime() != null && chart.getTimeType() == NAM) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue()).substring(0, 6) + "01";
                }
                return date;
            case END_YEAR:
                date = now.format(DateTimeFormatter.ofPattern("YYYY")) + "1231";
                return date;
            case BEGIN_MONTH:
                date = now.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMM)) + "01";
                now = LocalDate.parse(date, DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                if (chart.getRelativeTime() != null && chart.getTimeType() == NGAY) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue());
                }
                if (chart.getRelativeTime() != null && chart.getTimeType() == THANG) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue()).substring(0, 6) + "01";
                }
                if (chart.getRelativeTime() != null && chart.getTimeType() == QUY) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue()).substring(0, 6) + "01";
                }
                if (chart.getRelativeTime() != null && chart.getTimeType() == NAM) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue()).substring(0, 6) + "01";
                }
                return date;
            case END_MONTH:
                date = now.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMM)) + "01";
                LocalDate time = LocalDate.parse(date, DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD)); // ngay dau thang
                date = time.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));

                if (chart.getRelativeTime() != null && chart.getTimeType() == THANG) {
                    String beginM = dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue()).substring(0, 6) + "01";
                    LocalDate timeBeginM = LocalDate.parse(beginM, DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD)); // ngay dau thang
                    date = timeBeginM.plusMonths(1).minusDays(1).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                }

                return date;
            case BEGIN_QUAR:
                date = firstDayOfQuarter.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                now = firstDayOfQuarter;
                if (chart.getRelativeTime() != null && chart.getTimeType() == NGAY) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue());
                }
                if (chart.getRelativeTime() != null && chart.getTimeType() == THANG) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue());
                }
                if (chart.getRelativeTime() != null && chart.getTimeType() == QUY) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue());
                }
                if (chart.getRelativeTime() != null && chart.getTimeType() == NAM) {
                    return dateAfterChange(date, now, chart.getRelativeTime().intValue(), chart.getTimeType().intValue());
                }
                return date;
            case END_QUAR:
                date = lastDayOfQuarter.format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                return date;
        }
        return date;
    }

    private String dateAfterChange(String date, LocalDate now, int chartRelativeTime, int timetype) {
        switch (timetype) {
            case NGAY:
                if (chartRelativeTime > 0) {
                    date = now.plusDays(chartRelativeTime).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                } else if (chartRelativeTime < 0) {
                    date = now.minusDays(Math.abs(chartRelativeTime)).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
                }
                break;
        }
        if (timetype == THANG) {
            if (chartRelativeTime > 0) {
                date = now.plusMonths(chartRelativeTime).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
            } else if (chartRelativeTime < 0) {
                date = now.minusMonths(Math.abs(chartRelativeTime)).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
            }
        }
        if (timetype == QUY) {
            if (chartRelativeTime > 0) {
                date = now.plusMonths(3 * chartRelativeTime).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
            } else if (chartRelativeTime < 0) {
                date = now.minusMonths(3 * Math.abs(chartRelativeTime)).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
            }
        }
        if (timetype == NAM) {
            if (chartRelativeTime > 0) {
                date = now.plusYears(chartRelativeTime).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
            } else if (chartRelativeTime < 0) {
                date = now.minusYears(Math.abs(chartRelativeTime)).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT_YYYYMMDD));
            }
        }

        return date;
    }

    private void handleFromDateParamsValue(Map<String, Object> params,
                                           Map<String, Object> defaultParams,
                                           Integer toDate,
                                           Integer currentDate,
                                           Long timeType) throws ParseException {
        if (params.containsKey(Const.FILTER_PARAMS.FROM_DATE_PARAM)) {
            if (!DataUtil.isDate(String.valueOf(params.get(Const.FILTER_PARAMS.FROM_DATE_PARAM)), Const.DATE_FORMAT_YYYYMMDD)) {
                if (0 == toDate) toDate = currentDate;
                if (Arrays.asList(Const.PARAM_CHART_DEFAULT.MAX_DATE, Const.PARAM_CHART_DEFAULT.CURRENT_DATE, END_QUAR,
                        Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME)
                        .contains(defaultParams.get(Const.FILTER_PARAMS.TO_DATE_PARAM))) {
                    int rangeTime = DataUtil.safeToInt(params.get(Const.FILTER_PARAMS.FROM_DATE_PARAM));
                    if (0 == rangeTime) rangeTime = Const.DEFAULT_RANGE_TIME;
                    params.put(Const.FILTER_PARAMS.FROM_DATE_PARAM, String.valueOf(DataUtil.getAbsoluteDate(toDate, rangeTime, timeType)));
                }
                Integer fromDate = DataUtil.safeToInt(params.get(Const.FILTER_PARAMS.FROM_DATE_PARAM));
                if (fromDate > toDate) {
                    params.put(Const.FILTER_PARAMS.FROM_DATE_PARAM, String.valueOf(toDate));
                    params.put(Const.FILTER_PARAMS.TO_DATE_PARAM, String.valueOf(fromDate));
                } else {
                    params.put(Const.FILTER_PARAMS.FROM_DATE_PARAM, String.valueOf(fromDate));
                    params.put(Const.FILTER_PARAMS.TO_DATE_PARAM, String.valueOf(toDate));
                }
            }
        }
    }

    @Override
    public Map<String, String> getParamDefault(Map<String, Object> params) {
        Map<String, String> paramDefault = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (Const.PARAM_CHART_DEFAULT.MAX_DATE.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.MAX_DATE);
            }
            if (Const.PARAM_CHART_DEFAULT.MAX_DATE_NTIME.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.MAX_DATE_NTIME);
            }
            if (BEGIN_YEAR.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), BEGIN_YEAR);
            }
            if (Const.PARAM_CHART_DEFAULT.END_YEAR.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.END_YEAR);
            }
            if (Const.PARAM_CHART_DEFAULT.BEGIN_MONTH.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.BEGIN_MONTH);
            }
            if (Const.PARAM_CHART_DEFAULT.END_MONTH.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.END_MONTH);
            }
            if (Const.PARAM_CHART_DEFAULT.BEGIN_QUAR.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.BEGIN_QUAR);
            }
            if (Const.PARAM_CHART_DEFAULT.END_QUAR.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.END_QUAR);
            }
            if (Const.PARAM_CHART_DEFAULT.MAX_DATE_NDAY.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.MAX_DATE_NDAY);
            }
            if (Const.PARAM_CHART_DEFAULT.MAX_DATE_NDATE.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.MAX_DATE_NDATE);
            }
            if (Const.PARAM_CHART_DEFAULT.MAX_DATE_NMONTH.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.MAX_DATE_NMONTH);
            }
            if (Const.PARAM_CHART_DEFAULT.MAX_DATE_NQUAR.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.MAX_DATE_NQUAR);
            }
            if (Const.PARAM_CHART_DEFAULT.MAX_DATE_NYEAR.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.MAX_DATE_NYEAR);
            }
            if (Const.PARAM_CHART_DEFAULT.CURRENT_DATE.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.CURRENT_DATE);
            }
            if (Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME);
            }
            if (Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NMONTH.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NMONTH);
            }
            if (Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NQUAR.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NQUAR);
            }
            if (Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NYEAR.equals(entry.getValue())) {
                paramDefault.put(entry.getKey(), Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NYEAR);
            }
        }
        return paramDefault;
    }

    public int getRangeTimeFromMaxDate(String configStr) {
        Optional<CatItemDto> catItemDTO = Optional.empty();
        int rangeTime = Const.DEFAULT_RANGE_TIME;
        if (Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NDATE.equals(configStr)
                || Const.PARAM_CHART_DEFAULT.MAX_DATE_NDATE.equals(configStr)) {
            catItemDTO = catItemService.findByCode(Const.CAT_ITEM_CODE.NDATE_CATITEM);
        } else if (Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NMONTH.equals(configStr)
                || Const.PARAM_CHART_DEFAULT.MAX_DATE_NMONTH.equals(configStr)) {
            catItemDTO = catItemService.findByCode(Const.CAT_ITEM_CODE.NMONTH_CATITEM);
        } else if (Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NQUAR.equals(configStr)
                || Const.PARAM_CHART_DEFAULT.MAX_DATE_NQUAR.equals(configStr)) {
            catItemDTO = catItemService.findByCode(Const.CAT_ITEM_CODE.NQUAR_CATITEM);
        } else if (Const.PARAM_CHART_DEFAULT.CURRENT_DATE_RELATIVE_TIME_NYEAR.equals(configStr)
                || Const.PARAM_CHART_DEFAULT.MAX_DATE_NYEAR.equals(configStr)) {
            catItemDTO = catItemService.findByCode(Const.CAT_ITEM_CODE.NYEAR_CATITEM);
        }
        if (catItemDTO.isPresent()) {
            rangeTime = DataUtil.safeToInt(catItemDTO.get().getItemValue());
        }
        return rangeTime;
    }

    @Override
    public List<ServiceGBTDChartDto> getKpiInfoForItemOfResultChart(SaveChartItemDto saveItem, Object firstRecord, Long unitIdView) {
        List<ServiceGBTDChartDto> kpis = new ArrayList<>();
        if (!DataUtil.isNullOrEmpty(saveItem.getKpiInfos()) && !DataUtil.isNullOrEmpty(saveItem.getKpiInfos().get(0).getKpis())) {
            SaveKpiInfoDto saved = super.map(saveItem.getKpiInfos().get(0), SaveKpiInfoDto.class);
            saved.getKpis().forEach(kpi ->
                    serviceGBTDService.findByKpiIdWithRate(kpi.getServiceId(), unitIdView).ifPresent(kpis::add));
        }
        if (firstRecord != null) {
            Map firstRecordMap = JsonUtil.toMap(firstRecord);
            ServiceGBTDChartDto cgk = null;
            if (!DataUtil.isNullOrEmpty(firstRecordMap)) {
                if (DataUtil.isNullOrEmpty(saveItem.getKpiInfos()) || DataUtil.isNullOrEmpty(saveItem.getKpiInfos().get(0).getKpis())) {
                    if (firstRecordMap.containsKey(Const.KPI_ID_FIELD)) {
                        Long kpiId = Long.parseLong(firstRecordMap.get(Const.KPI_ID_FIELD).toString());
                        Optional<ServiceGBTDChartDto> kpiInfo = serviceGBTDService.findByKpiIdWithRate(kpiId, unitIdView);
                        if (kpiInfo.isPresent()) {
                            cgk = kpiInfo.get();
                        }
                    }
                }
                Optional<ConfigDisplayQueryDto> unitDisplayConfig = saveItem.getDisplayConfigs().stream()
                        .filter(d -> Const.UNIT_DISPLAY.equals(d.getColumnChart())
                                || Const.UNIT_DISPLAY.toLowerCase().equals(d.getColumnChart()))
                        .findFirst();
                if (unitDisplayConfig.isPresent() && firstRecordMap.containsKey(unitDisplayConfig.get().getColumnQuery())) {
                    if (null != cgk) {
                        cgk.setUnitName(String.valueOf(firstRecordMap.get(unitDisplayConfig.get().getColumnQuery())));
                    }
                }
                if (null != cgk) {
                    kpis.add(cgk);
                }
            }
        }
        return kpis;
    }

    @Override
    public String processChartTitle(ChartResultDto chart) {
        if (StringUtils.isEmpty(chart.getTitleChart())) return StringUtils.EMPTY;
        Map<String, Object> params = chart.getFilterParams();
        String title = chart.getTitleChart();
        if (!DataUtil.isNullOrEmpty(params)) {
            Long timeType = chart.getTimeType();
            if (params.containsKey(Const.FILTER_PARAMS.TIME_TYPE_PARAM)) {
                timeType = DataUtil.safeToLong(params.get(Const.FILTER_PARAMS.TIME_TYPE_PARAM).toString());
            }
            for (String key : params.keySet()) {
                if (title.contains(":" + key)) {
                    String value = String.valueOf(params.get(key));
                    if (Const.FILTER_PARAMS.TO_DATE_PARAM.equals(key) || Const.FILTER_PARAMS.FROM_DATE_PARAM.equals(key)) {
                        value = DataUtil.formatDatePattern(DataUtil.safeToInt(params.get(key)), "dd/MM/yyyy");
                        if (Const.TIME_TYPE.YEAR.equals(timeType)) {
                            value = String.format(
                                    "%s %s",
                                    MessageUtil.getMessage("common.year"),
                                    DataUtil.formatDatePattern(DataUtil.safeToInt(params.get(key)), "yyyy"));
                        } else if (Const.TIME_TYPE.QUARTER.equals(timeType)) {
                            value = String.format(
                                    "%s %s",
                                    MessageUtil.getMessage("common.quarter"),
                                    DataUtil.formatQuarterPattern(DataUtil.safeToInt(params.get(key))));
                        } else if (Const.TIME_TYPE.MONTH.equals(timeType)) {
                            value = String.format(
                                    "%s %s",
                                    MessageUtil.getMessage("common.month"),
                                    DataUtil.formatDatePattern(DataUtil.safeToInt(params.get(key)), "MM/yyyy"));
                        }
                    }
                    title = title.replaceAll(":" + key, value);
                }
            }
        }
        return title;
    }

    @Override
    public ChartResultDto saveChart(SaveChartDto dto) {
        List<ActionAuditDto> actionLogs = new ArrayList<>();
        ActionAuditDto.Builder logChart = dto.getLogBuilder()
                .tableName(Const.TABLE.CONFIG_CHART);
        if (dto.getId() != null) {
            iBuildChartServiceTemp.deleteItems(dto.getId(), actionLogs);
            logChart.oldValue(configChartRepository.findById(dto.getId()).orElseThrow(() -> new ServerException(ErrorCode.NOT_FOUND, Const.TABLE.CONFIG_CHART)));
        }
        ConfigChartEntity configChart = super.map(dto, ConfigChartEntity.class);
        configChart.setStatus(Const.STATUS.ACTIVE);
        configChart.setUpdateTime(new Date());
        configChart.setUpdateUser(super.getCurrentUsername());
        configChart = configChartRepository.save(configChart);
        actionLogs.add(dto.getLogBuilder()
                .objectId(configChart.getId())
                .newValue(configChart)
                .build()
        );
        dto.setId(configChart.getId());
        if (Const.CAT_ITEM_CODE.MAP_CHART_TYPE.equals(dto.getTypeChart())) {
            ConfigChartItemDto chartItem = new ConfigChartItemDto();
            chartItem.setTypeChart(Const.CAT_ITEM_CODE.ITEM_MAP_CHART_TYPE);
            chartItem.setChartId(configChart.getId());
            chartItem.setStatus(Const.STATUS.ACTIVE);
            chartItem.setUpdateTime(new Date());
            chartItem.setUpdateUser(super.getCurrentUsername());
            ConfigChartItemEntity item = configChartItemRepository.save(super.map(chartItem, ConfigChartItemEntity.class));
            actionLogs.add(super.insertLog(Const.TABLE.CONFIG_CHART_ITEM, item.getId(), item));
            return new ChartResultDto(dto);
        }
        Map<SaveChartItemDto, List<SaveChartItemDto>> mergedMap = this.mergeQueries(dto);
        for (List<SaveChartItemDto> items : mergedMap.values()) {
            ConfigQueryChartDto query = this.buildQuery(items, true, actionLogs);
            items.forEach(i -> {
                try {
                    ConfigChartItemDto chartItem = i.toDto();
                    chartItem.setQuery(query);
                    chartItem.setStatus(Const.STATUS.ACTIVE);
                    List<ConfigDisplayQueryDto> configDisplayQueryDtos = i.getColumns().stream()
                            .filter(c -> !DataUtil.isNullOrEmpty(c.getValues()) &&
                                    c.getValues().stream().anyMatch(v -> StringUtils.isNotEmpty(v.getValue())))
                            .collect(Collectors.toList());
                    chartItem.setDisplayConfigs(configDisplayQueryDtos);
                    if (query != null) {
                        chartItem.setQueryId(query.getId());
                        chartItem.setChartId(dto.getId());
                        chartItem.setUpdateTime(new Date());
                        chartItem.setUpdateUser(super.getCurrentUsername());
                        ConfigChartItemEntity entity = configChartItemRepository.save(super.map(chartItem, ConfigChartItemEntity.class));
                        actionLogs.add(super.insertLog(Const.TABLE.CONFIG_CHART_ITEM, entity.getId(), entity));
                        ConfigChartItemDto finalChartItem = super.map(entity, ConfigChartItemDto.class);
                        i.getColumns().forEach(c -> {
                            c.setItemChartId(finalChartItem.getId());
                            c.setStatus(Const.STATUS.ACTIVE);
                            c.setUpdateTime(new Date());
                            c.setUpdateUser(super.getCurrentUsername());
                            ConfigDisplayQueryEntity e = configDisplayQueryRepository.save(super.map(c, ConfigDisplayQueryEntity.class));
                            actionLogs.add(super.insertLog(Const.TABLE.CONFIG_DISPLAY_QUERY, e.getId(), e));
                        });
                    }
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
        super.saveLog(actionLogs);
        return new ChartResultDto(dto);
    }

    @Override
    public Map<SaveChartItemDto, List<SaveChartItemDto>> mergeQueries(SaveChartDto dto) {
        Map<SaveChartItemDto, List<SaveChartItemDto>> rs = new HashMap<>();
        if (DataUtil.isNullOrEmpty(dto.getItems())) return rs;
        long idx = 0;
        for (SaveChartItemDto i : dto.getItems()) {
            i.setId(null);
            i.setOrderIndex(idx);
            idx++;
            if (StringUtils.isEmpty(i.getCustomizeSql())) {
                if (DataUtil.isNullOrEmpty(i.getKpiInfos())) continue;
                List<String> tableNames = i.getKpiInfos().stream().map(SaveKpiInfoDto::getTableName).distinct().collect(Collectors.toList());
                if (tableNames.size() > 1) {
                    rs.put(i, new ArrayList<>(Collections.singletonList(i)));
                    continue;
                }
            }
            boolean existed = false;
            for (SaveChartItemDto key : rs.keySet()) {
                if (key.canMerge(i)) {
                    existed = true;
                    List<SaveChartItemDto> existedItems = rs.get(key);
                    existedItems.add(i);
                    break;
                }
            }
            if (!existed) {
                rs.put(i, new ArrayList<>(Collections.singletonList(i)));
            }
        }
        return rs;
    }

    @Override
    public ConfigQueryChartDto buildQuery(List<SaveChartItemDto> items, boolean hasSave, List<ActionAuditDto> actionLogs) {
        if (DataUtil.isNullOrEmpty(items)) return null;
        Map<String, SaveKpiInfoDto> tableKpiMapping = new HashMap<>();
        for (SaveChartItemDto item : items) {
            for (SaveKpiInfoDto kpi : item.getKpiInfos()) {
                if (tableKpiMapping.containsKey(kpi.getTableName())) {
                    SaveKpiInfoDto mergedKpi = tableKpiMapping.get(kpi.getTableName());
                    List<ServiceGBTDDto> kpis = mergedKpi.getKpis();
                    List<Long> existedKpiIds = kpis.stream().map(ServiceGBTDDto::getServiceId).collect(Collectors.toList());
                    kpis.addAll(kpi.getKpis().stream().filter(k -> !existedKpiIds.contains(k.getServiceId())).collect(Collectors.toList()));
                    mergedKpi.setKpis(kpis);
                    tableKpiMapping.put(kpi.getTableName(), mergedKpi);
                } else {
                    tableKpiMapping.put(kpi.getTableName(), kpi);
                }
            }
        }
        SaveChartItemDto firstItem = items.get(0);
        String whCls = StringUtils.EMPTY;
        String maxPrdIdWhCls = StringUtils.EMPTY;
        Map<String, Object> defaultValues = new HashMap<>();
        List<String> sqlQueries = new ArrayList<>();
        List<String> maxPrdSqlQueries = new ArrayList<>();
        boolean joinCatGraphKpi = items.stream().anyMatch(i -> i.getJoinCatGraphKpi() != null && i.getJoinCatGraphKpi());
        if (StringUtils.isNotEmpty(firstItem.getCustomizeSql())) {
            for (String tableName : tableKpiMapping.keySet()) {
                defaultValues.put(Const.FILTER_PARAMS.TABLE_NAME, tableName);
                iBuildChartServiceTemp.createDisplayQueries(items, tableName);
            }
            Map<String, Object> finalDefaultValues = defaultValues;
            firstItem.getParams().forEach(p -> {
                finalDefaultValues.put(p.getValue().substring(1), p.getValueDefault());
            });
            defaultValues = finalDefaultValues;
        } else {
            for (String tableName : tableKpiMapping.keySet()) {
                StringBuilder sql = new StringBuilder(), maxPrdSql = new StringBuilder();
                String orderByStr = createOrderBy(firstItem.getOrderBys(), tableName, false);
                SaveKpiInfoDto kpiInfo = tableKpiMapping.get(tableName);
                List<SaveDisplayQueryDto> columns = iBuildChartServiceTemp.createDisplayQueries(items, tableName);
                Map<String, Object> retWhCls = iBuildChartServiceTemp.createWhCls(items, tableName);
                if (!DataUtil.isNullOrEmpty(retWhCls)) {
                    whCls = (String) retWhCls.get("whCls");
                    maxPrdIdWhCls = (String) retWhCls.get("maxPrdIdWhCls");
                    defaultValues = (Map<String, Object>) retWhCls.get("defaultValues");
                }
                if (DataUtil.isNullOrEmpty(kpiInfo.getKpis())) continue;
                List<String> fields = columns.stream().map(SaveDisplayQueryDto::getFieldSql).collect(Collectors.toList());
                sql.append(" select ").append(Const.DATA_TABLE_ALIAS + ".SERVICE_ID DATA_SERVICE_ID, ").append(StringUtils.join(fields, ",")).append(" from ").append(":" + Const.FILTER_PARAMS.TABLE_NAME);
                maxPrdSql.append(" select prd_id from ").append(Const.TABLE.RPT_DATA_NEWEST);

                defaultValues.put(Const.FILTER_PARAMS.TABLE_NAME, tableName);
                sql.append(StringUtils.SPACE).append(Const.DATA_TABLE_ALIAS);
                maxPrdSql.append(StringUtils.SPACE).append(Const.DATA_TABLE_ALIAS);
                tableName = Const.DATA_TABLE_ALIAS;
                if (joinCatGraphKpi)
                    sql.append(" left outer join ").append(Const.TABLE.SERVICES_GBTD).append(StringUtils.SPACE).append(Const.KPI_TABLE_ALIAS).append(" on ").append(tableName).append(".service_id = ").append(Const.KPI_TABLE_ALIAS).append(".service_id ");
                sql.append(whCls).append(" and ").append(tableName).append(".service_id in (:").append(Const.FILTER_PARAMS.KPI_IDS_PARAM).append(") ");
                maxPrdSql.append(maxPrdIdWhCls).append(" and ").append(tableName).append(".service_id in (:").append(Const.FILTER_PARAMS.KPI_IDS_PARAM).append(") ");
                if (StringUtils.isNotEmpty(orderByStr)) {
                    sql.append(orderByStr);
                }
                if (StringUtils.isNotEmpty(firstItem.getLimit())) {
                    StringBuilder limitSubQuery = new StringBuilder("SELECT ").append("DATA_SERVICE_ID, ").append(StringUtils.join(columns.stream().map(SaveDisplayQueryDto::getColumnQuery).collect(Collectors.toList()), Const.SPECIAL_CHAR.COMMA)).append(" FROM (");
                    sql.insert(0, limitSubQuery);
                    sql.append(") ").append(" where ROWNUM <= ").append(firstItem.getLimit());
                }
                sqlQueries.add(sql.toString());
                maxPrdSqlQueries.add(maxPrdSql.toString());
            }
        }
        ConfigQueryChartDto rs = new ConfigQueryChartDto();
        rs.setQueryData(StringUtils.isEmpty(firstItem.getCustomizeSql()) ? StringUtils.join(sqlQueries, " union ") : firstItem.getCustomizeSql());
        rs.setQueryMaxPrdId(StringUtils.isEmpty(firstItem.getCustomizeMaxPrdIdSql()) ? "select max(prd_id) from ( " + StringUtils.join(maxPrdSqlQueries, " union ") + ") max_prd" : firstItem.getCustomizeMaxPrdIdSql());
        rs.setDefaultValue(JsonUtil.writeAsString(defaultValues));
        rs.setStatus(Const.STATUS.ACTIVE);
        rs.setUpdateTime(new Date());
        rs.setUpdateUser(super.getCurrentUsername());
        if (hasSave) {
            ConfigQueryChartEntity entity = configQueryChartRepository.save(super.map(rs, ConfigQueryChartEntity.class));
            actionLogs.add(super.insertLog(Const.TABLE.CONFIG_QUERY_CHART, entity.getId(), entity));
            rs = super.map(entity, ConfigQueryChartDto.class);
        }
        return rs;
    }

    @Override
    public String createOrderBy(List<SaveOrderByDto> orderBys, String tableName, boolean overview) {
        StringBuilder orderByStr = new StringBuilder();
        if (!DataUtil.isNullOrEmpty(orderBys)) {
            orderByStr.append(" order by ");
            int i = 0;
            for (SaveOrderByDto order : orderBys) {
                if (StringUtils.isEmpty(order.getSortDir())) {
                    order.setSortDir("asc");
                }
                if (StringUtils.isNotEmpty(order.getValue())) {
                    orderByStr.append(order.getValue()
                            .replace(tableName + ".", Const.DATA_TABLE_ALIAS + ".")
                            .replace(Const.TABLE.SERVICES_GBTD + ".", Const.KPI_TABLE_ALIAS + "."));
                    orderByStr.append(" ").append(order.getSortDir());
                }
                if (orderBys.size() > 1 && i < orderBys.size() - 1) {
                    orderByStr.append(", ");
                }
                i++;
            }
        }
        return orderByStr.toString();
    }
}
