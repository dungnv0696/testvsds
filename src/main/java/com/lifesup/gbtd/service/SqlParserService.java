package com.lifesup.gbtd.service;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.object.SaveDisplayQueryDto;
import com.lifesup.gbtd.dto.object.SaveDisplayQueryValueDto;
import com.lifesup.gbtd.dto.object.SaveInputParamDto;
import com.lifesup.gbtd.dto.object.SaveOrderByDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
import com.lifesup.gbtd.exception.ServerException;
import com.lifesup.gbtd.repository.ConfigChartRepository;
import com.lifesup.gbtd.service.inteface.ISqlParserService;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import net.sf.jsqlparser.util.deparser.StatementDeParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SqlParserService extends BaseService implements ISqlParserService {

    private final static String FIELD = "Field";
    private final static String DISPLAY_NAME = "DisplayName";
    private final ConfigChartRepository configChartRepository;

    @Autowired
    public SqlParserService(ConfigChartRepository configChartRepository) {
        this.configChartRepository = configChartRepository;
    }

    @Override
    public Map<String, String> getTableAlias(Select stmt) {
        Map<String, String> aliases = new TreeMap<>();
        StringBuilder buffer = new StringBuilder();
        ExpressionDeParser expressionDeParser = new ExpressionDeParser();
        SelectDeParser deparser = new SelectDeParser(expressionDeParser, buffer) {
            @Override
            public void visit(Table table) {
                String currentTableName = table.getName();
                if (table.getAlias() != null && !"".equals(table.getAlias().getName()))
                    aliases.put(table.getAlias().getName().toLowerCase(), currentTableName.toLowerCase());
                this.getBuffer().append(table);
            }
        };
        expressionDeParser.setSelectVisitor(deparser);
        expressionDeParser.setBuffer(buffer);
        StatementDeParser sdp = new StatementDeParser(expressionDeParser, deparser, buffer);
        stmt.accept(sdp);
        return aliases;
    }

    @Override
    public List<String> getTableNames(Select stmt) {
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        return tablesNamesFinder.getTableList(stmt);
    }

    @Override
    public List<SaveDisplayQueryDto> analyzeColumns(
            List<SaveDisplayQueryDto> displayQueries, PlainSelect plainSelect,
            Map<String, String> aliases, Map<String, List<Map<String, Object>>> tables) {
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        List<SaveDisplayQueryDto> rs = new ArrayList<>();

        if (!DataUtil.isNullOrEmpty(selectItems)) {
            for (SelectItem selectItem : selectItems) {
                if (selectItem instanceof AllColumns) {
                    for (Map.Entry<String, List<Map<String, Object>>> tableEntry : tables.entrySet()) {
                        rs.addAll(analyzeAllColumns(displayQueries, tableEntry.getKey(), tableEntry.getValue()));
                    }
                } else if (selectItem instanceof AllTableColumns) {
                    AllTableColumns allTableCols = (AllTableColumns) selectItem;
                    Table table = allTableCols.getTable();
                    if (!tables.containsKey(table.getName())) continue;
                    String tableName = table.getName();
                    List<Map<String, Object>> columns = tables.get(table.getName());
                    rs.addAll(analyzeAllColumns(displayQueries, tableName, columns));
                } else {
                    SelectExpressionItem sei = (SelectExpressionItem) selectItem;
                    Expression expression = sei.getExpression();
                    String columnQuery = StringUtils.EMPTY;
                    if (expression instanceof Column) {
                        columnQuery = ((Column) expression).getColumnName();
                    }
                    Alias alias = sei.getAlias();
                    if (alias != null) {
                        columnQuery = alias.getName();
                    }
                    List<SaveDisplayQueryValueDto> values = analyzeValueOfColumn(columnQuery, expression, tables);
                    if (displayQueries == null) {
                        SaveDisplayQueryDto item = new SaveDisplayQueryDto();
                        item.setColumnQuery(columnQuery);
                        item.setValues(values);
                        rs.add(item);
                    } else {
                        for (SaveDisplayQueryDto displayQuery : displayQueries) {
                            if (StringUtils.isNotEmpty(displayQuery.getColumnQuery())
                                    && columnQuery.toLowerCase().equals(displayQuery.getColumnQuery().toLowerCase())) {
                                displayQuery.setValues(values);
                            }
                        }
                    }
                }
            }
        }
        if (displayQueries == null) return rs;
        return displayQueries;
    }

    @Override
    public List<SaveDisplayQueryValueDto> analyzeValueOfColumn(
            String columnQuery, Expression expression,
            Map<String, List<Map<String, Object>>> tables) {
        List<SaveDisplayQueryValueDto> values = new ArrayList<>();

        if (expression instanceof Column) {
            SaveDisplayQueryValueDto value = new SaveDisplayQueryValueDto();
            if (StringUtils.isNotEmpty(columnQuery)) {
                value.setValue(((Column) expression).getColumnName());
                value.setLabel(columnQuery);
                value.setType(Const.FIELD_TYPE);
            } else {
                value = getValueFromColumn((Column) expression, tables);
            }
            values.add(value);
        } else {
            if (expression instanceof Function) {
                String name = ((Function) expression).getName();
                if ("concat".equals(name.toLowerCase())) {
                    ExpressionList paramExp = ((Function) expression).getParameters();
                    if (paramExp != null && !DataUtil.isNullOrEmpty(paramExp.getExpressions())) {
                        values = paramExp.getExpressions().stream()
                                .map(p -> {
                                    SaveDisplayQueryValueDto v = new SaveDisplayQueryValueDto();
                                    if (p instanceof Column) {
                                        if (StringUtils.isNotEmpty(columnQuery)) {
                                            v.setValue(((Column) p).getColumnName());
                                            v.setLabel(columnQuery);
                                            v.setType(Const.FIELD_TYPE);
                                        } else {
                                            v = getValueFromColumn((Column) p, tables);
                                        }
                                    }
                                    if (p instanceof StringValue) {
                                        v.setType(Const.TEXT_TYPE);
                                        v.setValue(((StringValue) p).getValue().toLowerCase());
                                        v.setLabel(p.toString());
                                    }
                                    if (p instanceof Function) {
                                        v.setType(Const.FUNCTION_TYPE);
                                        v.setLabel(p.toString());
                                        v.setValue(p.toString());
                                        v.setFunction(p.toString());
                                    }
                                    return v;
                                }).collect(Collectors.toList());
                    }
                } else {
                    SaveDisplayQueryValueDto value = new SaveDisplayQueryValueDto();
                    value.setType(Const.FUNCTION_TYPE);
                    value.setFunction(expression.toString());
                    value.setLabel(StringUtils.isNotEmpty(columnQuery) ? columnQuery : expression.toString());
                    value.setValue(expression.toString());
                    values.add(value);
                }
            } else {
                SaveDisplayQueryValueDto value = new SaveDisplayQueryValueDto();
                value.setType(Const.TEXT_TYPE);
                value.setLabel(StringUtils.isNotEmpty(columnQuery) ? columnQuery : expression.toString());
                value.setValue(expression.toString());
                values.add(value);
            }
        }
        return values;
    }

    private String getColumnLabel(String columnName, Map<String, List<Map<String, Object>>> tables) {
        String columnLabel = StringUtils.EMPTY;
        String finalColumnName = columnName.toLowerCase();
        Optional<Map.Entry<String, List<Map<String, Object>>>> table = tables.entrySet().stream()
                .filter(i -> i.getValue().stream()
                        .anyMatch(c -> finalColumnName.equals(((String) c.get(FIELD)).toLowerCase())))
                .findFirst();
        if (table.isPresent()) {
            Optional<Map<String, Object>> columnTable = table.get().getValue().stream()
                    .filter(c -> finalColumnName.equals(((String) c.get(FIELD)).toLowerCase()))
                    .findFirst();
            if (columnTable.isPresent())
                columnLabel = (String) columnTable.get().get(DISPLAY_NAME);

            columnName = table.get().getKey() + "." + finalColumnName;
        }
        return StringUtils.isNotEmpty(columnLabel) ? columnLabel : columnName;
    }

    @Override
    public String getFullColumnName(String columnName, Map<String, List<Map<String, Object>>> tables) {
        String finalColumnName = columnName.toLowerCase().replace(Const.DATA_TABLE_ALIAS + ".", "")
                .replace(Const.KPI_TABLE_ALIAS + ".", "");

        Optional<Map.Entry<String, List<Map<String, Object>>>> table = tables.entrySet().stream()
                .filter(i -> i.getValue().stream()
                        .anyMatch(c -> c.get(FIELD) != null && ((String) c.get(FIELD)).toLowerCase().endsWith(finalColumnName)))
                .findFirst();
        if (table.isPresent()) {
            columnName = table.get().getKey() + "." + finalColumnName;
        }
        return columnName;
    }

    private SaveDisplayQueryValueDto getValueFromColumn(Column column, Map<String, List<Map<String, Object>>> tables) {
        SaveDisplayQueryValueDto value = new SaveDisplayQueryValueDto();
        String columnName = column.getColumnName();
        String tableName = StringUtils.EMPTY;

        String finalColumnName = columnName;
        Optional<Map.Entry<String, List<Map<String, Object>>>> table = tables.entrySet().stream()
                .filter(i -> i.getValue().stream()
                        .anyMatch(c -> finalColumnName.toLowerCase().equals(((String) c.get(FIELD)).toLowerCase().replace(i.getKey() + ".", ""))))
                .findFirst();
        if (table.isPresent()) {
            tableName = table.get().getKey();
        }

        if (StringUtils.isNotEmpty(tableName)) {
            value.setType(Const.FIELD_TYPE);
            String columnLabel = getColumnLabel(columnName, tables);
            columnName = tableName + "." + column.getColumnName().toLowerCase();
            value.setLabel(StringUtils.isNotEmpty(columnLabel) ? columnLabel : columnName);
            value.setValue(columnName);
        } else {
            value.setValue(columnName);
            value.setLabel(columnName);
        }

        return value;
    }

    @Override
    public SaveInputParamDto analyzeWhCls(Expression exp, Map<String, Object> defaultValues, Map<String, List<Map<String, Object>>> tables) {

        SaveInputParamDto rs = new SaveInputParamDto();
        if ("1=1".equals(exp.toString().replaceAll(" ", "")))
            return null;
        if (!(exp instanceof ComparisonOperator) && !(exp instanceof InExpression)) return null;
        if (exp instanceof ComparisonOperator) {
            ComparisonOperator compareExp = (ComparisonOperator) exp;
            rs.setOperator(compareExp.getStringExpression());
            Expression left = compareExp.getLeftExpression();
            Expression right = compareExp.getRightExpression();
            String colName = StringUtils.EMPTY;
            if (left instanceof Column) {
                colName = ((Column) left).getColumnName();
            }
            if (right instanceof Column) {
                colName = ((Column) right).getColumnName();
            }
            if (StringUtils.isEmpty(colName)) return null;
            rs.setFieldName(getFullColumnName(colName, tables));
            String paramName = StringUtils.EMPTY;
            if (left instanceof JdbcNamedParameter) {
                paramName = ((JdbcNamedParameter) left).getName();
            }
            if (right instanceof JdbcNamedParameter) {
                paramName = ((JdbcNamedParameter) right).getName();
            }
            if (StringUtils.isNotEmpty(paramName)) {
                rs.setParamName(paramName);
                rs.setValue(":" + paramName);
                if (!DataUtil.isNullOrEmpty(defaultValues) && defaultValues.containsKey(paramName)) {
                    rs.setValueDefault(String.valueOf(defaultValues.get(paramName)));
                }
                rs.setIsFilterParam(true);
            }
            if (left instanceof LongValue || left instanceof StringValue) {
                rs.setValue(left.toString());
            }
            if (right instanceof LongValue || right instanceof StringValue) {
                rs.setValue(right.toString());
            }
        }
        if (exp instanceof InExpression) {
            InExpression inExp = (InExpression) exp;
            Expression left = inExp.getLeftExpression();
            String colName = StringUtils.EMPTY;
            if (left instanceof Column) {
                colName = ((Column) left).getColumnName();
            }
            if (StringUtils.isEmpty(colName)) return null;
            rs.setOperator(Const.IN_OPERATOR);
            if (inExp.isNot()) {
                rs.setOperator(Const.NOT_IN_OPERATOR);
            }
            rs.setParamName(colName);
            rs.setFieldName(getFullColumnName(colName, tables));
            ItemsList list = inExp.getLeftItemsList();
            if (list == null)
                list = inExp.getRightItemsList();
            if (list != null) {
                if (list instanceof ExpressionList) {
                    List<String> exps = ((ExpressionList) list).getExpressions().stream()
                            .map(Object::toString)
                            .collect(Collectors.toList());
                    rs.setValue(StringUtils.join(exps, ","));
                } else {
                    rs.setValue(list.toString());
                }
            }
        }
        return rs;
    }

    @Override
    public List<ServiceGBTDDto> parseKpiFromParam(List<SaveInputParamDto> params, Map<String, Object> defaultValues) {
        List<ServiceGBTDDto> kpis = new ArrayList<>();
        Optional<SaveInputParamDto> kpiParam = params.stream()
                .filter(p -> !DataUtil.isNullOrEmpty(p.getFieldName()) && p.getFieldName().contains(Const.KPI_ID_FIELD.toLowerCase()))
                .findFirst();

        if (kpiParam.isPresent()) {
            List<Long> kpiIds = new ArrayList<>();
            if ((":" + Const.FILTER_PARAMS.KPI_IDS_PARAM).equals(kpiParam.get().getValue())) {
                List<Object> defaultKpiIds = (List<Object>) defaultValues.get(Const.FILTER_PARAMS.KPI_IDS_PARAM);
                if (!DataUtil.isNullOrEmpty(defaultKpiIds))
                    kpiIds = defaultKpiIds.stream().map(DataUtil::safeToLong).collect(Collectors.toList());
            } else if (StringUtils.isNotEmpty(kpiParam.get().getValue())) {
                kpiIds = Arrays.stream(kpiParam.get().getValue().split(","))
                        .map(i -> DataUtil.safeToLong(i.trim())).collect(Collectors.toList());
            }
            kpiIds = kpiIds.stream().distinct().collect(Collectors.toList());
            if (!DataUtil.isNullOrEmpty(kpiIds)) {
                kpis = super.mapList(configChartRepository.findAllById(kpiIds), ServiceGBTDDto.class);
                kpis = kpis.stream()
                        .peek(k -> k.setServiceName(String.format("%s_%s", k.getServiceId(), k.getServiceName())))
                        .collect(Collectors.toList());
            }
        }

        return kpis;
    }

    @Override
    public List<SaveOrderByDto> analyzeOrderBy(List<OrderByElement> orderByElms,
                                               Map<String, List<Map<String, Object>>> tables) {
        List<SaveOrderByDto> orderBys = new ArrayList<>();
        if (!DataUtil.isNullOrEmpty(orderByElms)) {
            orderBys = orderByElms.stream().map(o -> {
                SaveOrderByDto dto = new SaveOrderByDto();
                Expression exp = o.getExpression();
                if (exp instanceof Column) {
                    dto.setValue(getFullColumnName(((Column) exp).getColumnName(), tables));
                    dto.setType(Const.FIELD_TYPE);
                    dto.setLabel(getColumnLabel(((Column) exp).getColumnName(), tables));
                } else if (exp instanceof Function) {
                    dto.setLabel(exp.toString());
                    dto.setFunction(exp.toString());
                    dto.setType(Const.FUNCTION_TYPE);
                } else {
                    dto.setValue(exp.toString());
                    dto.setLabel(exp.toString());
                    dto.setType(Const.TEXT_TYPE);
                }

                dto.setSortDir(o.isAsc() ? "asc" : "desc");
                return dto;
            }).collect(Collectors.toList());
        }

        return orderBys;
    }

    @Override
    public List<SaveDisplayQueryDto> analyzeAllColumns(List<SaveDisplayQueryDto> displayQueries, String tableName, List<Map<String, Object>> columns) {
        List<SaveDisplayQueryDto> rs = new ArrayList<>();

        for (Map<String, Object> column : columns) {
            String columnQuery = (String) column.get(FIELD);
            SaveDisplayQueryValueDto value = new SaveDisplayQueryValueDto();
            value.setLabel((String) column.get(DISPLAY_NAME));
            value.setType(Const.FIELD_TYPE);
            value.setValue(tableName + "." + columnQuery);
            if (displayQueries == null) {
                SaveDisplayQueryDto item = new SaveDisplayQueryDto();
                item.setColumnQuery(tableName + "." + columnQuery);
                item.setValues(Collections.singletonList(value));
                rs.add(item);
            } else {
                for (SaveDisplayQueryDto displayQuery : displayQueries) {
                    if (StringUtils.isEmpty(displayQuery.getColumnQuery())) continue;
                    if (columnQuery.toLowerCase().equals(displayQuery.getColumnQuery().toLowerCase())) {
                        displayQuery.setValues(Collections.singletonList(value));
                    }
                }
            }
        }
        return displayQueries == null ? rs : displayQueries;
    }

    @Override
    public String analyzeLimit(Limit limit) {
        if (limit != null && !limit.isLimitNull()) {
            return limit.toString().replaceAll("(?i)limit", "").trim();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public List<Map<String, Object>> getAllColumns(Map<String, String> aliases, List<String> tableNames, Map<String, List<Map<String, Object>>> tables) {
        List<Map<String, Object>> allColumns = new ArrayList<>();
        for (String tableName : tableNames) {
            Optional<String> alias = aliases.entrySet().stream()
                    .filter(e -> tableName.equals(e.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst();
            try {
                List<Map<String, Object>> desColumns = new ArrayList<>();
                configChartRepository.getDescriptionOfTableToMap(tableName).forEach(e -> {
                    Map<String, Object> map = JsonUtil.toMap(e);
                    desColumns.add(map);
                });
                allColumns.addAll(desColumns);
                tables.put(alias.orElse(tableName), desColumns);
            } catch (Exception e) {
                log.error("error", e);
                throw new ServerException(ErrorCode.NOT_FOUND, tableName);
            }
        }

        return allColumns;
    }
}
