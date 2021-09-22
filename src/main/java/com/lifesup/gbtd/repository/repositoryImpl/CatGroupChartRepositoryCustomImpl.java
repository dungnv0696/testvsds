package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.CatGroupChartDto;
import com.lifesup.gbtd.repository.CatGroupChartRepositoryCustom;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CatGroupChartRepositoryCustomImpl implements CatGroupChartRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<CatGroupChartDto> findAllCatGroups(Long dashboardId, Long status, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT ");
        sql.append("cgc.id AS id, ");
        sql.append("cgc.group_name AS groupName, ");
        sql.append("cgc.kpi_id_main AS groupKpiCode, ");
        sql.append("cgc.kpi_id_main AS kpiIdMain, ");
        sql.append("cgc.description AS description, ");
        sql.append("cgc.status AS status, ");
        sql.append("cgc.update_time AS updateTime, ");
        sql.append("cgc.update_user AS updateUser ");
        sql.append("FROM cat_group_chart cgc ");
        sql.append("INNER JOIN config_chart cc ON cgc.id = cc.group_chart_id AND cc.status = :status " +
                "INNER JOIN config_map_chart_area cmca ON cc.id = cmca.chart_id AND cmca.status = :status " +
                "INNER JOIN config_area ca ON cmca.area_id = ca.id ");
        sql.append("WHERE 1 = 1 ");
        if (Objects.nonNull(dashboardId)) {
            sql.append("AND ca.dashboard_id = :dashboardId ");
            params.put("dashboardId", dashboardId);
        }
        sql.append("ORDER BY cgc.group_name ASC");
        status = status != null ? status : Const.STATUS.ACTIVE;
        params.put("status", status);

        String sqlCount = "SELECT COUNT(*) FROM ( " + sql.toString() + " )";
        Query queryCount = em.createNativeQuery(sqlCount);
        JpaUtil.setQueryParams(queryCount, params);

        Query query = em.createNativeQuery(sql.toString(), "catGroupChart")
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        JpaUtil.setQueryParams(query, params);

        List<CatGroupChartDto> configProfileDtos = query.getResultList();
        Long count = ((BigDecimal) queryCount.getSingleResult()).longValue();
        return new PageImpl<>(configProfileDtos, pageable, count);
    }

    @Override
    public Page<CatGroupChartDto> findAllCatGroups(CatGroupChartDto dto, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append("cgc.id AS id, ");
        sql.append("cgc.group_name AS groupName, ");
        sql.append("cgc.group_kpi_code AS groupKpiCode, ");
        sql.append("cgc.kpi_id_main AS kpiIdMain, ");
        sql.append("cgc.description AS description, ");
        sql.append("cgc.status AS status, ");
        sql.append("cgc.update_time AS updateTime, ");
        sql.append("cgc.update_user AS updateUser ");
        sql.append("FROM cat_group_chart cgc ");
        sql.append("WHERE 1 = 1 ");
        if (Objects.nonNull(dto.getGroupName())) {
            sql.append("AND lower(cgc.group_name) like :groupName ");
            params.put("groupName", DataUtil.makeLikeParam(dto.getGroupName()));
        }

        if (Objects.nonNull(dto.getGroupKpiCode())) {
            sql.append("AND cgc.group_kpi_code = :groupKpiCode ");
            params.put("groupKpiCode", dto.getGroupKpiCode());
        }

        if (Objects.nonNull(dto.getKpiIdMain())) {
            sql.append("AND cgc.kpi_id_main = :kpiIdMain ");
            params.put("kpiIdMain", dto.getKpiIdMain());
        }

        sql.append("AND cgc.status = :status ");
        if (Objects.nonNull(dto.getStatus())) {
            params.put("status", dto.getStatus());
        } else {
            params.put("status", Const.STATUS.ACTIVE);
        }

        String sqlCount = "SELECT COUNT(*) FROM ( " + sql.toString() + " )";
        Query queryCount = em.createNativeQuery(sqlCount);
        JpaUtil.setQueryParams(queryCount, params);

        Query query = em.createNativeQuery(sql.toString(), "catGroupChart")
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        JpaUtil.setQueryParams(query, params);

        List<CatGroupChartDto> catGroupChartDtos = query.getResultList();
        Long count = ((BigDecimal) queryCount.getSingleResult()).longValue();
        return new PageImpl<>(catGroupChartDtos, pageable, count);
    }
}
