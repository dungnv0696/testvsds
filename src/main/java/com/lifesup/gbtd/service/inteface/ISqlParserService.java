package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.SaveDisplayQueryDto;
import com.lifesup.gbtd.dto.object.SaveDisplayQueryValueDto;
import com.lifesup.gbtd.dto.object.SaveInputParamDto;
import com.lifesup.gbtd.dto.object.SaveOrderByDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.List;
import java.util.Map;

public interface ISqlParserService {
    Map<String, String> getTableAlias(Select stmt);

    List<String> getTableNames(Select stmt);

    List<SaveDisplayQueryDto> analyzeColumns(
            List<SaveDisplayQueryDto> displayQueries, PlainSelect plainSelect,
            Map<String, String> aliases, Map<String, List<Map<String, Object>>> tables);

    List<SaveDisplayQueryValueDto> analyzeValueOfColumn(
            String columnQuery, Expression expression,
            Map<String, List<Map<String, Object>>> tables);

    String getFullColumnName(String columnName, Map<String, List<Map<String, Object>>> tables);

    SaveInputParamDto analyzeWhCls(Expression exp, Map<String, Object> defaultValues, Map<String, List<Map<String, Object>>> tables);

    List<ServiceGBTDDto> parseKpiFromParam(List<SaveInputParamDto> params, Map<String, Object> defaultValues);

    List<SaveOrderByDto> analyzeOrderBy(List<OrderByElement> orderByElms, Map<String, List<Map<String, Object>>> tables);

    abstract List<SaveDisplayQueryDto> analyzeAllColumns(List<SaveDisplayQueryDto> displayQueries, String tableName, List<Map<String, Object>> columns);

    String analyzeLimit(Limit limit);

    List<Map<String, Object>> getAllColumns(Map<String, String> aliases, List<String> tableNames, Map<String, List<Map<String, Object>>> tables);
}
