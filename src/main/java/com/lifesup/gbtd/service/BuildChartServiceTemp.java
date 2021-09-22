package com.lifesup.gbtd.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.ActionAuditDto;
import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.dto.object.ConfigQueryChartDto;
import com.lifesup.gbtd.dto.object.SaveChartItemDto;
import com.lifesup.gbtd.dto.object.SaveDisplayQueryDto;
import com.lifesup.gbtd.dto.object.SaveDisplayQueryValueDto;
import com.lifesup.gbtd.dto.object.SaveInputParamDto;
import com.lifesup.gbtd.dto.object.SaveKpiInfoDto;
import com.lifesup.gbtd.dto.object.SaveOrderByDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.model.ConfigChartItemEntity;
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
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BuildChartServiceTemp extends BaseService implements IBuildChartServiceTemp {

    private final Set<String> rptDataNewestField
            = new HashSet<>(Arrays.asList("SERVICE_ID", "INPUT_LEVEL", "TIME_TYPE", "PRD_ID", "OBJ_CODE"));

    private final ICatItemService catItemService;
    private final ConfigChartItemRepository configChartItemRepository;
    private final ConfigDisplayQueryRepository configDisplayQueryRepository;
    private final ConfigQueryChartRepository configQueryChartRepository;
    private final ISqlParserService sqlParserService;

    @Autowired
    public BuildChartServiceTemp(
            ICatItemService catItemService,
            ConfigChartItemRepository configChartItemRepository,
            ConfigDisplayQueryRepository configDisplayQueryRepository,
            ConfigQueryChartRepository configQueryChartRepository,
            ISqlParserService sqlParserService) {
        this.catItemService = catItemService;
        this.configChartItemRepository = configChartItemRepository;
        this.configDisplayQueryRepository = configDisplayQueryRepository;
        this.configQueryChartRepository = configQueryChartRepository;
        this.sqlParserService = sqlParserService;
    }

    @Override
    public List<SaveDisplayQueryDto> createDisplayQueries(List<SaveChartItemDto> items, String tableName) {
        List<SaveDisplayQueryDto> columns = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            SaveChartItemDto item = items.get(i);
            List<SaveDisplayQueryDto> colItems = item.getColumns();
//            if (StringUtils.isNotEmpty(item.getCustomizeSql())) {
//                columns.addAll(colItems);
//                continue;
//            }
            for (int j = 0; j < colItems.size(); j++) {
                SaveDisplayQueryDto column = colItems.get(j);
                column.setColumnQuery("item" + i + "_col" + j);
                if (DataUtil.isNullOrEmpty(column.getValues())) continue;
                StringBuilder field = new StringBuilder();
                StringBuilder value = new StringBuilder();
                column.getValues().forEach(e -> {
                    if (e.getValue().contains(",")) {
                        e.setValue(e.getValue().replace(",", "rdc"));
                    }
                });

                for (int k = 0; k < column.getValues().size(); k++) {
                    if (k != 0) value.append(", ");
                    SaveDisplayQueryValueDto val = column.getValues().get(k);
                    if ("field".equals(val.getType()) && "TEXT".equals(column.getDataType()) && !"THRESHOLD".equals(column.getColumnChart()) && val.getValue().indexOf("NAME") == -1 && val.getValue().indexOf("DESCRIPT") == -1
                            && val.getValue().indexOf("CODE") == -1 && val.getValue().indexOf("TIME") == -1 && val.getValue().indexOf("ID") == -1 && val.getValue().indexOf("TYPE") == -1 && val.getValue().indexOf("USER") == -1
                            && val.getValue().indexOf("STATUS") == -1 && val.getValue().indexOf("INDEX") == -1 && val.getValue().indexOf("FORMULA") == -1) {
                        value.append(val.getValue());
//                        value.append("TO_CHAR(").append(val.getValue()).append(": '999G999G990D99')");
                    } else {
                        value.append(val.getValue());
                    }
//                    if (Const.DISPLAY_QUERY_TYPE.FIELD.equals(val.getType())) {
//                        if (overview) {
//                            value.append(val.getValue().replace(tableName + ".", Const.DATA_TABLE_ALIAS + ".")
////                                    .replace("cat_graph_kpi.", Const.KPI_TABLE_ALIAS + "."));
//                                    .replace(Const.TABLE.SERVICES_GBTD + ".", Const.KPI_TABLE_ALIAS + "."));
//                        } else {
//                            value.append(
//                                    val.getValue().replace(Const.DATA_TABLE_ALIAS + ".", val.getValue().contains(tableName) ? "" : tableName + ".")
////                                            .replace(Const.KPI_TABLE_ALIAS + ".", (val.getValue().contains("cat_graph_kpi") ? "" : "cat_graph_kpi.")));
//                                            .replace(Const.KPI_TABLE_ALIAS + ".", (val.getValue().contains(Const.TABLE.SERVICES_GBTD) ? "" : Const.TABLE.SERVICES_GBTD + ".")));
//                        }
//                    } else {
//                        value.append(val.getValue());
//                    }
                }
                if (column.getValues().size() > 1) {
                    int comma = value.indexOf(",");
                    while (comma != -1) {
                        value.replace(comma, comma + 1, " || ' ' || ");
                        comma = value.indexOf(",");
                    }
//                    value.insert(0, " CONCAT(");
//                    value.append(") ");
                }
                if ("TEXT".equals(column.getDataType())) {
                    int index1 = value.indexOf("TO_CHAR(");
                    if (index1 != -1) {
                        int index2 = value.indexOf(":", index1);
                        int index3 = value.indexOf(")", index1);
                        while (index2 != -1) {
                            value.replace(index2, index2 + 1, ",");
                            if (value.indexOf(":", index1) > index3) {
                                index1 = value.indexOf("TO_CHAR(", index2);
                                if (index1 == -1) {
                                    index2 = -1;
                                } else {
                                    index2 = value.indexOf(":", index1);
                                    index3 = value.indexOf(")", index1);
                                }
                            } else {
                                index2 = value.indexOf(":", index1);
                            }
                        }
                    }
                }
                field.append(value.toString().replace("rdc", ","));
                field.append(" as ").append(column.getColumnQuery());
                column.setFieldSql(field.toString());
                columns.add(column);
            }
        }
        return columns;
    }

    @Override
    public Map<String, Object> createWhCls(List<SaveChartItemDto> items, String tableName) {
        if (DataUtil.isNullOrEmpty(items)) return new HashMap<>();
        SaveChartItemDto firstItem = items.get(0);
        boolean hasMaxPrdIdQuery = firstItem.getParams().stream()
                .anyMatch(p -> StringUtils.isNotEmpty(p.getValueDefault()) && (p.getValueDefault().contains(Const.PARAM_CHART_DEFAULT.MAX_DATE)
                        ||p.getValueDefault().contains(Const.PARAM_CHART_DEFAULT.BEGIN_QUAR)
                        || p.getValueDefault().contains(Const.PARAM_CHART_DEFAULT.END_QUAR)
                        ||p.getValueDefault().contains(Const.PARAM_CHART_DEFAULT.BEGIN_YEAR)
                        || p.getValueDefault().contains(Const.PARAM_CHART_DEFAULT.END_YEAR)
                        || p.getValueDefault().contains(Const.PARAM_CHART_DEFAULT.BEGIN_MONTH)
                        || p.getValueDefault().contains(Const.PARAM_CHART_DEFAULT.END_MONTH)
                        || p.getValueDefault().contains(Const.PARAM_CHART_DEFAULT.MAX_DATE_NTIME)
                )); // => True

        StringBuilder whCls = new StringBuilder(" where 1 = 1 ");
        StringBuilder maxPrdIdWhCls = new StringBuilder(" where 1 = 1 ");
        Map<String, Object> defaultValues = new HashMap<>();

        Pattern r = Pattern.compile("^:[A-Z]+$");
        Pattern r2 = Pattern.compile("^\\(.*\\)$");

        CatItemDto catItemCriteria = new CatItemDto();
        catItemCriteria.setCategoryCodes(Collections.singletonList(Const.CAT_ITEM_CODE.PARAM_CHART_CATITEM));
        List<CatItemDto> paramCatItems = catItemService.findAll(catItemCriteria);

        for (SaveInputParamDto param : firstItem.getParams()) {
            if (StringUtils.isEmpty(param.getValue())) continue; //Vao

            if (r.matcher(param.getValue()).find()) {
                param.setIsFilterParam(true); //vao
            }
            if (paramCatItems.stream().anyMatch(c -> param.getValue().equals(c.getItemValue()))) {
                param.setIsFilterParam(true); //vao
            }
            String value = param.getValue();
            if (this.isValidOperator(param.getOperator())) {
                value = String.format("(%s)", param.getValue()); //khong vao
            }
            if (param.getIsFilterParam() != null && param.getIsFilterParam()) {
//                if (StringUtils.isEmpty(param.getValueDefault())) {
//                    throw new ServerException(ErrorCode.MISSING_PARAMS, "defaultValue");
//                }
                if (r2.matcher(value).find()) { //khong vao
                    value = String.format("(%s)", param.getValue().toUpperCase());
                } else {
                    value = param.getValue().toUpperCase(); //vao
                }
                if (this.isValidOperator(param.getOperator()) && StringUtils.isNotEmpty(param.getValueDefault())) { //khong vao
                    String valueT = value.substring(2, value.length() - 1);
                    List<String> ls = Arrays.asList(param.getValueDefault().split(","));
                    ls = ls.stream().map(String::trim).collect(Collectors.toList());
                    defaultValues.put(param.getValue().startsWith(":") ? valueT : value, ls);
                } else {
                    defaultValues.put(param.getValue().startsWith(":") ? value.substring(1) : value, param.getValueDefault()); //vao
                }
            }
            //
//            String colName = param.getFieldName()
//                    .replace(Const.DATA_TABLE_ALIAS + ".", "");
//                    .replace(tableName + ".", "");
//            colName = (overview ? Const.DATA_TABLE_ALIAS + "." : tableName + ".") + colName;
            // if value = timetype skip where for sql data
            if (!param.getValue().toUpperCase().contains(Const.FILTER_PARAMS.TIME_TYPE_PARAM)) {
                whCls.append(" and ")
                        .append(param.getFieldName())
                        .append(" ")
                        .append(param.getOperator())
                        .append(" ")
                        .append(value);
            }

            if (hasMaxPrdIdQuery && StringUtils.isNotEmpty(param.getFieldName())
                    && !(Const.DATA_PRD_ID.equals(param.getFieldName()) || (param.getFieldName().contains(".")
                    && Const.DATA_PRD_ID.equals(param.getFieldName().substring(param.getFieldName().lastIndexOf(".") + 1).toLowerCase())))
                    && rptDataNewestField.contains(param.getFieldName().substring(param.getFieldName().lastIndexOf(".") + 1).toUpperCase())) {
                maxPrdIdWhCls.append(" and ")
                        .append(param.getFieldName())
                        .append(" ")
                        .append(param.getOperator())
                        .append(" ")
                        .append(value);
            }
        }
        List<Long> kpiIds = items.stream()
                .flatMap(i -> i.getKpiInfos().stream())
                .flatMap(k -> k.getKpis().stream())
                .map(ServiceGBTDDto::getServiceId).distinct()
                .collect(Collectors.toList());
        defaultValues.put(Const.FILTER_PARAMS.KPI_IDS_PARAM, kpiIds);

        Map<String, Object> rs = new HashMap<>();
        rs.put("whCls", whCls.toString());
        rs.put("maxPrdIdWhCls", maxPrdIdWhCls.toString());
        rs.put("defaultValues", defaultValues);
        return rs;
    }

    @Override
    public boolean isValidOperator(String operator) {
        return StringUtils.isNotEmpty(operator) &&
                (Const.IN_OPERATOR.equals(operator.toUpperCase()) || Const.NOT_IN_OPERATOR.equals(operator.toUpperCase()));
    }

    @Override
    public SaveChartItemDto generateInputCondition(SaveChartItemDto item, ConfigQueryChartDto query, List<SaveDisplayQueryDto> displayQueries) throws JsonProcessingException {
        SaveChartItemDto rs = new SaveChartItemDto(item);
        if (StringUtils.isEmpty(query.getQueryData())) return rs;
        String sql = query.getQueryData();
        List<SaveKpiInfoDto> kpiInfos = new ArrayList<>();
        List<SaveOrderByDto> orderBys;
        List<SaveInputParamDto> params = new ArrayList<>();
        Map<String, Object> defaultValues = StringUtils.isNotEmpty(query.getDefaultValue()) ? JsonUtil.toMap(query.getDefaultValue()) : new HashMap<>();
        defaultValues = mergeParams(defaultValues, null);
        Map<String, List<Map<String, Object>>> tables = new TreeMap<>();
        List<Map<String, Object>> allColumns = new ArrayList<>();
        List<SaveDisplayQueryDto> outColumns = new ArrayList<>();
        try {
            if (sql.contains(":" + Const.FILTER_PARAMS.TABLE_NAME)) {
                if (defaultValues.containsKey(Const.FILTER_PARAMS.TABLE_NAME)) {
                    sql = sql.replace(":" + Const.FILTER_PARAMS.TABLE_NAME, (String) defaultValues.get(Const.FILTER_PARAMS.TABLE_NAME));
                } else if (!DataUtil.isNullOrEmpty(item.getKpiInfos())) {
                    String tableName = item.getKpiInfos().get(0).getTableName();
                    sql = sql.replace(":" + Const.FILTER_PARAMS.TABLE_NAME, tableName);
                    defaultValues.put(Const.FILTER_PARAMS.TABLE_NAME, tableName);
                } else {
                    sql = sql.replace(":" + Const.FILTER_PARAMS.TABLE_NAME, Const.TABLE.RPT_GRAPH_DAY);
                    defaultValues.put(Const.FILTER_PARAMS.TABLE_NAME, Const.TABLE.RPT_GRAPH_DAY);
                }
            }
            Statement stmt = CCJSqlParserUtil.parse(sql);
            if (stmt instanceof Select) {
                Select select = (Select) stmt;
                Map<String, String> aliases = sqlParserService.getTableAlias(select);
                List<String> tableNames = sqlParserService.getTableNames(select);
                allColumns = sqlParserService.getAllColumns(aliases, tableNames, tables);
                SelectBody selectBody = select.getSelectBody();
                if (selectBody instanceof PlainSelect) {
                    PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
                    rs.setLimit(sqlParserService.analyzeLimit(plainSelect.getLimit()));
                    displayQueries = sqlParserService.analyzeColumns(displayQueries, plainSelect, aliases, tables);
                    outColumns = sqlParserService.analyzeColumns(null, plainSelect, aliases, tables);
                    orderBys = sqlParserService.analyzeOrderBy(plainSelect.getOrderByElements(), tables);
                    Expression whCls = plainSelect.getWhere();
                    List<ServiceGBTDDto> kpis = new ArrayList<>();
                    if (whCls != null) {
                        do {
                            Expression left = ((BinaryExpression) whCls).getLeftExpression();
                            Expression right = ((BinaryExpression) whCls).getRightExpression();
                            if (!(left instanceof BinaryExpression) && !(right instanceof BinaryExpression)) {
                                params.add(sqlParserService.analyzeWhCls(whCls, defaultValues, tables));
                            } else {
                                params.add(sqlParserService.analyzeWhCls(right != null ? right : left, defaultValues, tables));
                            }
                            whCls = left;
                        } while (whCls instanceof BinaryExpression);
                        params = params.stream().filter(Objects::nonNull).collect(Collectors.toList());
                        kpis = sqlParserService.parseKpiFromParam(params, defaultValues);
                    }
                    params = params.stream().filter(p -> DataUtil.isNullOrEmpty(p.getFieldName()) || !p.getFieldName().contains(Const.KPI_ID_FIELD.toLowerCase())).collect(Collectors.toList());
                    tableNames = tableNames.stream().filter(t -> !t.contains("cat_graph_kpi")).collect(Collectors.toList());
                    for (String tableName : tableNames) {
                        SaveKpiInfoDto kpiInfo = new SaveKpiInfoDto(tableName, kpis);
                        kpiInfos.add(kpiInfo);
                    }
                } else {
                    throw new ServerException(ErrorCode.NOT_VALID, "input");
                }
            } else {
                throw new ServerException(ErrorCode.NOT_VALID, "statement");
            }
        } catch (JSQLParserException e) {
            log.error("error", e);
            throw new ServerException(ErrorCode.NOT_VALID, "sql");
        }
        query.setDefaultValue(JsonUtil.writeAsString(defaultValues));rs.setColumns(displayQueries);rs.setKpiInfos(DataUtil.isNullOrEmpty(kpiInfos) || kpiInfos.stream().allMatch(k -> DataUtil.isNullOrEmpty(k.getKpis())) ? item.getKpiInfos() : kpiInfos);rs.setOrderBys(DataUtil.isNullOrEmpty(orderBys) ? item.getOrderBys() : orderBys);rs.setParams(DataUtil.isNullOrEmpty(params) ? item.getParams() : params);rs.setCustomizeSql(query.getQueryData());rs.setCustomizeMaxPrdIdSql(query.getQueryMaxPrdId());rs.setAllColumns(allColumns);rs.setOutColumns(outColumns);rs.setQuery(query);
        return rs;
    }

    @Override
    public void deleteItems(Long chartId, List<ActionAuditDto> actionLogs) {
        List<ConfigChartItemEntity> itemEntities = configChartItemRepository.findByChartId(chartId);
        List<Long> itemIds = new ArrayList<>();
        List<Long> queryIds = new ArrayList<>();
        itemEntities.forEach(e -> {
            itemIds.add(e.getId());
            queryIds.add(e.getQueryId());
            actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_CHART_ITEM, e.getId(), e));
            configChartItemRepository.delete(e);
        });

        configQueryChartRepository.deleteAll(
                configQueryChartRepository.findByIdIn(queryIds)
                        .stream()
                        .peek(e -> actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_QUERY_CHART, e.getId(), e)))
                        .collect(Collectors.toList()));

        configDisplayQueryRepository.deleteAll(
                configDisplayQueryRepository.findByItemChartIdIn(itemIds)
                        .stream()
                        .peek(e -> actionLogs.add(super.deleteLog(Const.TABLE.CONFIG_DISPLAY_QUERY, e.getId(), e)))
                        .collect(Collectors.toList()));
    }

    @Override
    public Map<String, Object> mergeParams(Map<String, Object> defaultParams, Map<String, Object> params) {
        if (DataUtil.isNullOrEmpty(defaultParams) && DataUtil.isNullOrEmpty(params)) return new HashMap<>();
        if (DataUtil.isNullOrEmpty(defaultParams)) {
            return params;
        }
        if (DataUtil.isNullOrEmpty(params)) {
            params = new HashMap<>();
        }
        Map<String, Object> temp = new HashMap<>();
        try {
            for (String key : defaultParams.keySet()) {
//            Xu ly toan tu like
                if ("MONTH".equals(key)) {
                    String tempValue = "%____" + this.processData(defaultParams.get(key).toString()) + "__%";
                    temp.put(key.toUpperCase(), tempValue);
                } else if ("DAY".equals(key)) {
                    String tempValue = "%______" + this.processData(defaultParams.get(key).toString()) + "%";
                    temp.put(key.toUpperCase(), tempValue);
                } else if ("YEAR".equals(key)) {
                    String tempValue = "%" + defaultParams.get(key) + "____%";
                    temp.put(key.toUpperCase(), tempValue);
                } else if ("QUAR".equals(key)) {
                    String tempValue = "%____" + this.processDataQuar(defaultParams.get(key).toString()) + "__%";
                    temp.put(key.toUpperCase(), tempValue);
                } else {
                    temp.put(key.toUpperCase(), defaultParams.get(key));
                }
            }
        } catch (Exception e) {
            log.error("parse error", e);
            throw new ServerException(ErrorCode.NOT_VALID, Const.TABLE.CONFIG_CHART);
        }

        //defaultParams;
        if (!DataUtil.isNullOrEmpty(params)) {
            if (params.containsKey(Const.FILTER_PARAMS.TIME_TYPE_PARAM)) {
                Long timeType = Long.valueOf((String) params.get(Const.FILTER_PARAMS.TIME_TYPE_PARAM));
                if (Const.TIME_TYPE_TABLE_MAP.containsKey(timeType)) {
                    params.put(Const.FILTER_PARAMS.TABLE_NAME, Const.TIME_TYPE_TABLE_MAP.get(timeType));
                } else {
                    params.remove(Const.FILTER_PARAMS.TIME_TYPE_PARAM);
                }
            }
            for (String key : params.keySet()) {
                temp.put(key, params.get(key));
            }
        }
        return temp;
    }

    private String processDataQuar(String str) {
        String result = "";
//        try {
        if ("1".equals(str) || "01".equals(str)) {
            result = "01";
        } else if ("2".equals(str) || "02".equals(str)) {
            result = "04";
        } else if ("3".equals(str) || "03".equals(str)) {
            result = "07";
        } else if ("4".equals(str) || "04".equals(str)) {
            result = "10";
        } else {
            throw new ServerException(ErrorCode.NOT_VALID, Const.TABLE.CONFIG_CHART);
        }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            throw new ServerException(ErrorCode.NOT_VALID);
//        }
        return result;
    }

    private String processData(String str) {
        String result = "";
//        try {
        String temp = "0".concat(str);
        result += temp.substring(temp.length() - 2);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            throw new ServerException(ErrorCode.NOT_VALID);
//        }

        return result;
    }

}
