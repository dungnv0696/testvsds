package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.repository.ExecuteSqlRepository;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional
@Slf4j
public class ExecuteSqlRepositoryImpl implements ExecuteSqlRepository {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Integer getMaxPrdId(String sql, Map<String, Object> params) {
        if (sql.contains(":" + Const.FILTER_PARAMS.TABLE_NAME)) {
            if (params.containsKey(Const.FILTER_PARAMS.TABLE_NAME)) {
                sql = sql.replace(":" + Const.FILTER_PARAMS.TABLE_NAME, (String) params.get(Const.FILTER_PARAMS.TABLE_NAME));
            } else {
                sql = sql.replace(":" + Const.FILTER_PARAMS.TABLE_NAME, Const.TABLE.RPT_GRAPH_DAY);
            }
        }

        sql = changeSqlByObjOrParentCode(sql, params);

        Query query = manager.createNativeQuery(sql);
        if (!DataUtil.isNullOrEmpty(params)) {
            for (Parameter<?> queryParam : query.getParameters()) {
                Optional<Object> value = params.entrySet().stream()
                        .filter(i -> queryParam.getName().equals(i.getKey().toUpperCase()))
                        .map(Map.Entry::getValue).findFirst();
                value.ifPresent(o -> query.setParameter(queryParam.getName(), o));
            }
        }
//        String defaultParam = mapper.writeValueAsString(params);
        log.info(String.valueOf(params));
        List<?> rs = query.getResultList();
        if (DataUtil.isNullOrEmpty(rs)) return null;
        return rs.get(0) == null ? null : ((Number) rs.get(0)).intValue();
    }

    @Override
    public List<Object> executeSql(String sql, Map<String, Object> params) {
        if (StringUtils.isEmpty(sql)) return new ArrayList<>();
        if (sql.contains(":" + Const.FILTER_PARAMS.TABLE_NAME) && params.containsKey(Const.FILTER_PARAMS.TABLE_NAME)) {
            sql = sql.replace(":" + Const.FILTER_PARAMS.TABLE_NAME, (String) params.get(Const.FILTER_PARAMS.TABLE_NAME));
        }
        if (params.containsKey(Const.FILTER_PARAMS.DEPT_CODES)) {
            sql += "and (OBJ_CODE IN (:DEPT_CODES) or PARENT_CODE IN (:DEPT_CODES)) ";
        }
        sql = changeSqlByObjOrParentCode(sql, params);

        if (params.containsKey(Const.FILTER_PARAMS.BRANCH_CODE)) {
            int index1;
            StringBuilder sqlTemp = new StringBuilder(sql);
            index1 = sqlTemp.indexOf("1 = 1") + 5;
            if (index1 > 4)
                sqlTemp.replace(index1, index1 + 1, " AND DATA.TYPE_PARAM IN (:BRANCH_CODE) ");
            sql = new String(sqlTemp);
        }
        if(params.containsKey("rowNum")){
            sql = sql.split("where ROWNUM")[0] + "where ROWNUM <= " + params.get("rowNum");
        }
        if (params.containsKey(Const.FILTER_PARAMS.DRILL_DOWN)){
            sql = changeSqlByDrillDown(sql,params);
        }
        Query q = manager.createNativeQuery(sql);
        if (!DataUtil.isNullOrEmpty(params)) {
            for (Parameter<?> queryParam : q.getParameters()) {
                Optional<Object> value = params.entrySet().stream()
                        .filter(i -> queryParam.getName().equals(i.getKey().toUpperCase()))
                        .map(Map.Entry::getValue)
                        .findFirst();
                value.ifPresent(o -> q.setParameter(queryParam.getName(), o));
            }
        }
        org.hibernate.query.Query hibernateQuery = q.unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return hibernateQuery.list();
    }

    public String changeSqlByObjOrParentCode(String sql, Map<String, Object> params) {
        if (params.containsKey(Const.FILTER_PARAMS.OBJ_OR_PARENT_CODE)) {
            int index1 = -1;
            boolean checkObj = false, checkParent = false;
            if (sql.indexOf("and data.OBJ_CODE = :OBJCODE") >= 0) {
                checkObj = true;
                index1 = sql.indexOf("and data.OBJ_CODE = :OBJCODE");
                sql = sql.replace("and data.OBJ_CODE = :OBJCODE", " ");
            }
            if (sql.indexOf("and data.OBJ_CODE IN (:OBJCODE)") >= 0) {
                checkObj = true;
                index1 = sql.indexOf("and data.OBJ_CODE IN (:OBJCODE)");
                sql = sql.replace("and data.OBJ_CODE IN (:OBJCODE)", " ");
            }
            if (sql.indexOf("and data.PARENT_CODE = :PARENTCODE") >= 0) {
                checkParent = true;
                index1 = sql.indexOf("and data.PARENT_CODE = :PARENTCODE");
                sql = sql.replace("and data.PARENT_CODE = :PARENTCODE", " ");
            }
            if (sql.indexOf("and data.PARENT_CODE IN (:PARENTCODE)") >= 0) {
                checkParent = true;
                index1 = sql.indexOf("and data.PARENT_CODE IN (:PARENTCODE)");
                sql = sql.replace("and data.PARENT_CODE IN (:PARENTCODE)", " ");
            }
            StringBuilder sqlTemp = new StringBuilder(sql);
            if (index1 == -1) {
                index1 = sqlTemp.indexOf("1 = 1") + 5;
            }
            if (checkObj && checkParent) {
                sqlTemp.replace(index1, index1 + 1, " AND (DATA.OBJ_CODE IN (:OBJ_OR_PARENT_CODE) AND DATA.PARENT_CODE IN (:OBJ_OR_PARENT_CODE)) ");
            } else if (checkObj) {
                sqlTemp.replace(index1, index1 + 1, " AND DATA.OBJ_CODE IN (:OBJ_OR_PARENT_CODE) ");
            } else if (checkParent) {
                sqlTemp.replace(index1, index1 + 1, " AND DATA.PARENT_CODE IN (:OBJ_OR_PARENT_CODE) ");
            } else {
                sqlTemp.replace(index1, index1 + 1, " AND DATA.OBJ_CODE IN (:OBJ_OR_PARENT_CODE) ");
            }
            sql = new String(sqlTemp);
        }
        return sql;
    }

    public String changeSqlByDrillDown(String sql,Map<String, Object> params){
        if (!sql.contains("ata.OBJ_CODE") && !sql.contains("data.PARENT_CODE")){
            if (sql.contains("RPT_GRAPH_MON")){
                sql = sql.replace("from RPT_GRAPH_MON data where 1 = 1","from RPT_GRAPH_MON data where 1 = 1 and data.OBJ_CODE IN (:OBJECTCODE) ");
            } else if (sql.contains("RPT_GRAPH_DAY")){
                sql = sql.replace("from RPT_GRAPH_DAY data where 1 = 1","from RPT_GRAPH_DAY data where 1 = 1 and data.OBJ_CODE IN (:OBJECTCODE) ");
            } else if (sql.contains("RPT_GRAPH_YEAR")){
                sql = sql.replace("from RPT_GRAPH_YEAR data where 1 = 1","from RPT_GRAPH_YEAR data where 1 = 1 and data.OBJ_CODE IN (:OBJECTCODE) ");
            } else if (sql.contains("RPT_GRAPH_QUAR")){
                sql = sql.replace("from RPT_GRAPH_QUAR data where 1 = 1","from RPT_GRAPH_QUAR data where 1 = 1 and data.OBJ_CODE IN (:OBJECTCODE) ");
            }

        }

        sql = sql.replace(":OBJCODE",":OBJECTCODE");
        sql = sql.replace(":PARENTCODE",":OBJECTCODE");

        if ("0".equals(params.get(Const.FILTER_PARAMS.DRILL_DOWN).toString())){
            sql = sql.replace("and data.OBJ_CODE","and data.PARENT_CODE");
        } else if ("1".equals(params.get(Const.FILTER_PARAMS.DRILL_DOWN).toString())){
            if (sql.contains("RPT_GRAPH_MON")){
                sql = sql.replace("from RPT_GRAPH_MON data","from RPT_GRAPH_MON data\n" +
                        "INNER JOIN BI_TD_SERVICES_TREE bi\n" +
                        "on data.SERVICE_ID = bi.SERVICE_ID");
            } else if (sql.contains("RPT_GRAPH_DAY")){
                sql = sql.replace("from RPT_GRAPH_DAY data","from RPT_GRAPH_DAY data\n" +
                        "INNER JOIN BI_TD_SERVICES_TREE bi\n" +
                        "on data.SERVICE_ID = bi.SERVICE_ID");
            } else if (sql.contains("RPT_GRAPH_YEAR")){
                sql = sql.replace("from RPT_GRAPH_YEAR data","from RPT_GRAPH_YEAR data\n" +
                        "INNER JOIN BI_TD_SERVICES_TREE bi\n" +
                        "on data.SERVICE_ID = bi.SERVICE_ID");
            } else if (sql.contains("RPT_GRAPH_QUAR")){
                sql = sql.replace("from RPT_GRAPH_QUAR data","from RPT_GRAPH_QUAR data\n" +
                        "INNER JOIN BI_TD_SERVICES_TREE bi\n" +
                        "on data.SERVICE_ID = bi.SERVICE_ID");
            }
//            if (!sql.contains("and data.OBJ_CODE") || !sql.contains("and data.PARENT_CODE")){
//                sql += "and data.OBJ_CODE   IN (:OBJCODE)";
//            }
            sql = sql.replace("and data.service_id","AND bi.PARENT_SERVICE_ID");
        }
        return sql;
    }
}
