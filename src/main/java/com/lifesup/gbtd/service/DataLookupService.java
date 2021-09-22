package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.*;
import com.lifesup.gbtd.dto.request.DataLookupRequest;
import com.lifesup.gbtd.dto.response.DataLookupResponse;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.CatItemEntity;
import com.lifesup.gbtd.repository.CatItemRepository;
import com.lifesup.gbtd.repository.ConfigChartDefaultRepository;
import com.lifesup.gbtd.repository.ConfigChartRepository;
import com.lifesup.gbtd.repository.DataLookupRepository;
import com.lifesup.gbtd.service.inteface.IDataLookupService;
import com.lifesup.gbtd.service.inteface.IServiceGBTDService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataLookupService extends BaseService implements IDataLookupService {

    private final UserLogService userLogService;
    private final ConfigChartRepository configChartRepository;
    private final DataLookupRepository dataLookupRepository;
    private final CatItemRepository catItemRepository;
    private final ConfigChartDefaultRepository configChartDefaultRepository;
    private final IServiceGBTDService serviceGBTDService;
    private final SimpleDateFormat sdf;
    private String SELECT_FIELD_TABLE_RPT;

    @Autowired
    public DataLookupService(UserLogService userLogService, ConfigChartRepository configChartRepository,
                             DataLookupRepository dataLookupRepository,
                             CatItemRepository catItemRepository,
                             ConfigChartDefaultRepository configChartDefaultRepository,
                             IServiceGBTDService serviceGBTDService) {
        this.userLogService = userLogService;
        this.configChartRepository = configChartRepository;
        this.dataLookupRepository = dataLookupRepository;
        this.catItemRepository = catItemRepository;
        this.configChartDefaultRepository = configChartDefaultRepository;
        this.serviceGBTDService = serviceGBTDService;
        sdf = new SimpleDateFormat("yyyyMMdd");
    }

    @Override
    public Long getLatestDate(Long[] serviceIds, String[] deptCodes, Long timeType) {
        if (DataUtil.isNullOrEmpty(serviceIds) || null == timeType) {
            throw new ServerException(ErrorCode.MISSING_PARAMS);
        }
        Long date = dataLookupRepository.getLatestDate(serviceIds, deptCodes, timeType);
        if (null == date) {
            throw new ServerException(ErrorCode.NOT_FOUND, "data");
        }
        return date;
    }

    private String getFieldTableRpt() {
        if (StringUtils.isEmpty(SELECT_FIELD_TABLE_RPT)) {
            StringJoiner joiner = new StringJoiner(", ");
            List<TableDto> fields = configChartRepository.getDescriptionOfTableToMap(Const.TABLE.RPT_GRAPH_DAY);
            fields.forEach(f -> {
//                String alias = f.getField().toLowerCase();
//                while (alias.contains("_")) {
//                    alias = alias.replaceFirst("_[a-z]",
//                            String.valueOf(Character.toUpperCase(alias.charAt(alias.indexOf("_") + 1))));
//                }
//                joiner.add(Const.DATA_TABLE_ALIAS + "." + f.getField() + " " + alias);
                joiner.add(Const.DATA_TABLE_ALIAS + "." + f.getField());
            });
            SELECT_FIELD_TABLE_RPT = joiner.toString();
        }

        return SELECT_FIELD_TABLE_RPT;
    }

    private String createQueryLookup(DataLookupRequest rq) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
        sqlBuilder.append(this.getFieldTableRpt()).append(Const.SPECIAL_CHAR.COMMA)
                .append(Const.DATA_TABLE_ALIAS).append(Const.SPECIAL_CHAR.DOT).append(rq.getColumn()).append(" as ").append(Const.LOOKUP_FIELD_NAME.VALUE_SHOW).append(Const.SPECIAL_CHAR.COMMA)
                .append("data.obj_name ||' ('|| data.service_name || ')'").append(" as ").append(Const.LOOKUP_FIELD_NAME.CUSTOM_FIELD)
//                .append(Const.DATA_TABLE_ALIAS).append(Const.SPECIAL_CHAR.DOT).append("PRD_ID as ").append(Const.LOOKUP_FIELD_NAME.VALUE_SHOW)
                .append(" FROM ").append(Const.TIME_TYPE_TABLE_MAP.get(rq.getTimeType())).append(Const.SPECIAL_CHAR.SPACE).append(Const.DATA_TABLE_ALIAS)
                .append(" LEFT JOIN ").append(Const.TABLE.SERVICES_GBTD).append(Const.SPECIAL_CHAR.SPACE).append(Const.KPI_TABLE_ALIAS)
                .append(" on data.SERVICE_ID = kpi.SERVICE_ID and kpi.STATUS = 1 ")
                .append(" where data.prd_id >= ").append(JpaUtil.toQueryParam(Const.FILTER_PARAMS.FROM_DATE_PARAM))
                .append(" and data.prd_id <= ").append(JpaUtil.toQueryParam(Const.FILTER_PARAMS.TO_DATE_PARAM));

        if (!rq.getLookupByFormula()) {
            sqlBuilder.append(" and data.service_id in (").append(JpaUtil.toQueryParam(Const.FILTER_PARAMS.KPI_IDS_PARAM)).append(") ")
                    .append(" and data.obj_code in (").append(JpaUtil.toQueryParam(Const.FILTER_PARAMS.OBJCODES)).append(") ");
        } else {
            sqlBuilder.append(" and data.service_id||'_'||data.obj_code in (")
                    .append(JpaUtil.toQueryParam(Const.FILTER_PARAMS.SERVICE_DEPTS)).append(") ");
        }
        return sqlBuilder.toString();
    }

    @Override
    public DataLookupResponse doLookup(DataLookupRequest rq) {
        this.validateRequest(rq);
        String sql = this.createQueryLookup(rq);

        if (null == rq.getFromTime()) {
            Integer fromTime;
            try {
                fromTime = DataUtil.getAbsoluteDate(rq.getFromTime(), this.getBackTimeValue(rq.getTimeType()), rq.getTimeType());
            } catch (ParseException e) {
                log.error("parse error", e);
                throw new ServerException(ErrorCode.NOT_VALID, "fromTime");
            }
            rq.setFromTime(fromTime);
        }
        rq.setFromTime(DataUtil.transformDateByTimeType(rq.getFromTime(), rq.getTimeType()));
        rq.setToTime(DataUtil.transformDateByTimeType(rq.getToTime(), rq.getTimeType()));

        Map<String, Object> params = this.createQueryParams(rq);

        List<Object> data = dataLookupRepository.executeGetDataSql(sql, params);
        List<ConfigChartDefaultDto> cfgs = super.mapList(configChartDefaultRepository.findLookupParams(rq.getTypeChart(), rq.getTimeType()), ConfigChartDefaultDto.class);
        List<ServiceGBTDChartDto> kpiInfos;
        if (rq.getLookupByFormula()) {
            kpiInfos = rq.getChildren().stream()
                    .map(s -> serviceGBTDService.findByKpiIdWithRate(s.getServiceId(), rq.getUnitViewId())
                            .orElse(null))
                    .collect(Collectors.toList());
        } else {
            kpiInfos = rq.getServiceIds().stream()
                    .map(serviceId -> serviceGBTDService.findByKpiIdWithRate(serviceId, rq.getUnitViewId())
                            .orElse(null))
                    .collect(Collectors.toList());
        }

        ConfigQueryChartDto queryInfo = new ConfigQueryChartDto();
        queryInfo.setQueryData(sql);

        ChartDetailDto detailDto = new ChartDetailDto();
        detailDto.setChartType(rq.getTypeChart());
        detailDto.setData(data);
        detailDto.setDefaultConfigs(cfgs);
        detailDto.setKpiInfos(kpiInfos);
        detailDto.setQuery(queryInfo);
        DataLookupResponse res = new DataLookupResponse();
        res.setDetails(Collections.singletonList(detailDto));
        res.setFilterParams(params);
        //ghi log
//        UserLogDto userLogDto = new UserLogDto("POST", "SEARCH TRA_CUU_SO_LIEU", "Tìm kiếm tra cứu số liệu",objectToJson(rq));
//        userLogService.saveLog(userLogDto);
        return res;
    }

    private Map<String, Object> createQueryParams(DataLookupRequest rq) {
        Map<String, Object> map = new HashMap<>();
//        map.put(Const.FILTER_PARAMS.COLUMN, rq.getColumn());
        map.put(Const.FILTER_PARAMS.FROM_DATE_PARAM, rq.getFromTime());
        map.put(Const.FILTER_PARAMS.TO_DATE_PARAM, rq.getToTime());
//        map.put(Const.FILTER_PARAMS.TABLE_NAME, Const.TIME_TYPE_TABLE_MAP.get(rq.getTimeType()));

        if (!rq.getLookupByFormula()) {
            map.put(Const.FILTER_PARAMS.KPI_IDS_PARAM, rq.getServiceIds());
            map.put(Const.FILTER_PARAMS.OBJCODES, rq.getDepts().stream().map(CatDepartmentDto::getDeptCode).collect(Collectors.toList()));
        } else {
            ServiceGBTDDto criteria = new ServiceGBTDDto();
            criteria.setServiceId(rq.getServiceIds().get(0));
            criteria.setDeptId(rq.getDepts().get(0).getDeptId());
            List<ServiceGBTDDto> childrenServices = serviceGBTDService.findChildrenService(criteria);
            rq.setChildren(childrenServices);
            if (DataUtil.isNullOrEmpty(childrenServices)) {
                throw new ServerException(ErrorCode.NOT_FOUND, "children service");
            }
            map.put(Const.FILTER_PARAMS.SERVICE_DEPTS, childrenServices.stream()
                    .map(s -> s.getServiceId() + Const.SPECIAL_CHAR.UNDERSCORE + s.getDeptCode())
                    .collect(Collectors.toList())
            );
        }

        return map;
    }

    private Integer getBackTimeValue(Long timeType) {
        CatItemDto dto = new CatItemDto();
        dto.setParentCategoryCodes(new String[]{Const.CAT_ITEM_CODE.TIME_TYPE});
        dto.setParentValue(timeType.toString());
        dto.setCategoryCode(Const.CAT_ITEM_CODE.DURATION_TIME);
        List<CatItemEntity> list = catItemRepository.findAll(dto);
        if (DataUtil.isNullOrEmpty(list)) {
            throw new ServerException(ErrorCode.NOT_FOUND, Const.CAT_ITEM_CODE.DURATION_TIME);
        }
        return Integer.parseInt(list.get(0).getItemValue());
    }

    private void validateRequest(DataLookupRequest rq) {
        if (StringUtils.isEmpty(rq.getTypeChart())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "typeChart");
        }
        if (null == rq.getTimeType()) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "timeType");
        } else if (StringUtils.isEmpty(Const.TIME_TYPE_TABLE_MAP.get(rq.getTimeType()))) {
            throw new ServerException(ErrorCode.NOT_VALID, "timeType");
        }
        if (StringUtils.isEmpty(rq.getColumn())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "column");
        }
//        if (null == rq.getUnitViewId()) {
//            throw new ServerException(ErrorCode.MISSING_PARAMS, "unitViewId");
//        }
//        if (StringUtils.isEmpty(rq.getTableName())) {
//            throw new ServerException(ErrorCode.MISSING_PARAMS, "tableName");
//        } else if (!Arrays.asList(
//                Const.TABLE.RPT_GRAPH_DAY,
//                Const.TABLE.RPT_GRAPH_MON,
//                Const.TABLE.RPT_GRAPH_QUAR,
//                Const.TABLE.RPT_GRAPH_YEAR).contains(rq.getTableName().toUpperCase())) {
//            throw new ServerException(ErrorCode.NOT_VALID, "tableName");
//        }
        if (null == rq.getToTime()) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "toTime");
        }
        if (DataUtil.isNullOrEmpty(rq.getServiceIds())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "serviceIds");
        }
        if (DataUtil.isNullOrEmpty(rq.getDepts())) {
            throw new ServerException(ErrorCode.MISSING_PARAMS, "deptCodes");
        }
        if (null == rq.getLookupByFormula() && (rq.getServiceIds().size() > 1 || rq.getDepts().size() > 1)) {
            throw new ServerException(ErrorCode.NOT_VALID);
        }
    }
}