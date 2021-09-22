package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.ConfigChartDto;
import com.lifesup.gbtd.dto.object.TableDto;
import com.lifesup.gbtd.model.ConfigChartEntity;
import com.lifesup.gbtd.model.ConfigChartEntity_;
import com.lifesup.gbtd.model.ConfigDashboardEntity;
import com.lifesup.gbtd.repository.ConfigChartRepositoryCustom;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigChartRepositoryCustomImpl implements ConfigChartRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    private String createQueryFindChart() {
        return "SELECT " +
                "a.ID, " +
                "a.CHART_NAME, " +
                "a.TITLE_CHART, " +
                "a.TYPE_CHART, " +
                "a.ROLE_TYPE, " +
                "a.TIME_TYPE, " +
                "a.RELATIVE_TIME, " +
                "a.UNIT_ID_VIEW, " +
                "a.GROUP_CHART_ID, " +
                "a.CHART_CONFIG, " +
                "a.CHART_ID_NEXTTO, " +
                "a.CHART_ID_DEPEND, " +
                "a.ORDER_INDEX, " +
                "a.STATUS, " +
                "a.DESCRIPTION, " +
                "a.UPDATE_TIME, " +
                "a.UPDATE_USER, " +
                "a.DEPT_ID_SERVICE, " +
                "a.DEPT_ID_SERVICEs " +
                "FROM " +
                "(SELECT cc.ID, cc.CHART_NAME, cc.TITLE_CHART, cc.TYPE_CHART, cc.ROLE_TYPE, cc.TIME_TYPE, cc.RELATIVE_TIME, " +
                "cc.UNIT_ID_VIEW, cc.GROUP_CHART_ID, cc.CHART_CONFIG, cc.CHART_ID_NEXTTO, cc.CHART_ID_DEPEND, cc.ORDER_INDEX, " +
                "cc.STATUS, cc.DESCRIPTION, cc.UPDATE_TIME, cc.UPDATE_USER, cc.DEPT_ID_SERVICE, cc.DEPT_ID_SERVICEs " +
                "FROM config_chart cc , config_chart_role cr " +
                "WHERE cc.STATUS = 1 " +
                "AND cc.ID = cr.CHART_ID " +
                "AND cc.ROLE_TYPE = 1 " +
                "AND cr.DEPT_ID IN (:deptId ) " +
                "UNION ALL " +
                "SELECT " +
                "cc.ID, cc.CHART_NAME, cc.TITLE_CHART, cc.TYPE_CHART, cc.ROLE_TYPE, cc.TIME_TYPE, cc.RELATIVE_TIME, " +
                "cc.UNIT_ID_VIEW, cc.GROUP_CHART_ID, cc.CHART_CONFIG, cc.CHART_ID_NEXTTO, cc.CHART_ID_DEPEND, cc.ORDER_INDEX, " +
                "cc.STATUS, cc.DESCRIPTION, cc.UPDATE_TIME, cc.UPDATE_USER, cc.DEPT_ID_SERVICE, cc.DEPT_ID_SERVICEs " +
                "FROM config_chart cc , config_chart_role cr " +
                "WHERE cc.STATUS = 1 " +
                "AND cc.ID = cr.CHART_ID " +
                "AND cc.ROLE_TYPE = 2 " +
                "AND cr.USERNAME_USED IN (:username ) ) a " +
                "WHERE 1=1 ";
    }

    @Override
    public List<ConfigChartEntity> findUserChartByType(ConfigChartDto dto, List<Long> userDeptIds, List<String> userNames) {
        HashMap<String, Object> params = new HashMap<>();
        String sql = this.createQueryFindChart();
        // filter
        if (StringUtils.isNotEmpty(dto.getTypeChart())) {
            sql += "AND a.TYPE_CHART = :typeChart ";
            params.put("typeChart", dto.getTypeChart());
        }

        Query query = em.createNativeQuery(sql, ConfigChartEntity.class)
                .setParameter("deptId", userDeptIds)
                .setParameter("username", userNames);
        JpaUtil.setQueryParams(query, params);
        return query.getResultList();
    }

    @Override
    public Page<ConfigChartEntity> doSearch(ConfigChartDto dto, List<Long> userDeptIds, List<String> userNames, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("deptId", userDeptIds);
        params.put("username", userNames);

        String sql = this.createQueryFindChart();
        // filter
        if (StringUtils.isNotEmpty(dto.getTypeChart())) {
            sql += "AND a.TYPE_CHART = :typeChart ";
            params.put("typeChart", dto.getTypeChart());
        }
        if (StringUtils.isNotEmpty(dto.getKeyword())) {
            sql += "AND LOWER(a.TITLE_CHART) like :keyword escape '" + Const.DEFAULT_ESCAPE_CHAR + "' ";
            params.put("keyword", DataUtil.makeLikeParam(dto.getKeyword()));
        }
        if (StringUtils.isNotEmpty(dto.getChartName())) {
            sql += "AND LOWER(a.CHART_NAME) like :chartName escape '" + Const.DEFAULT_ESCAPE_CHAR + "' ";
            params.put("chartName", DataUtil.makeLikeParam(dto.getChartName()));
        }
        sql += "order by a.ID desc ";

        Query query = em.createNativeQuery(sql, ConfigChartEntity.class);
        Query queryCount = em.createNativeQuery(JpaUtil.toCountQuery(sql));

        return JpaUtil.getPageableResult(query, queryCount, params, pageable);
    }

    @Override
    public List<TableDto> getDescriptionOfTableToMap(String tableName) {
        StringBuffer sql = new StringBuffer("SELECT a.column_name AS field, ");
        sql.append("a.data_type AS type, ");
        sql.append("a.column_name AS displayName, ");
        sql.append("b.COMMENTS AS \"comment\" ");
        sql.append("FROM USER_TAB_COLUMNS a, USER_COL_COMMENTS b ");
        sql.append("WHERE a.table_name = :tableName ");
        sql.append("AND a.TABLE_NAME = b.table_name AND a.COLUMN_NAME = b.COLUMN_NAME ");

        Query query = em.createNativeQuery(sql.toString(), "configChart.tableDesc");
        query.setParameter("tableName", tableName);
        return query.getResultList();
    }

    @Override
    public List<ConfigChartEntity> findWithFilter(List<Long> ids, ConfigChartDto filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ConfigChartEntity> criteria = cb.createQuery(ConfigChartEntity.class);
        Root<ConfigChartEntity> root = criteria.from(ConfigChartEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (!DataUtil.isNullOrEmpty(ids)) {
            predicates.add(root.get(ConfigChartEntity_.ID).in(ids));
        }
        if (null != filter.getTimeType()) {
            predicates.add(cb.equal(root.get(ConfigChartEntity_.TIME_TYPE), filter.getTimeType()));
        }
        criteria.where(predicates.toArray(new Predicate[] {}));
        return em.createQuery(criteria).getResultList();
    }
}
