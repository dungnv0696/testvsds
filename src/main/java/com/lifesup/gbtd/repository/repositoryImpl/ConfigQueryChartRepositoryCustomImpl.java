package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.ConfigQueryChartDto;
import com.lifesup.gbtd.repository.ConfigQueryChartRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ConfigQueryChartRepositoryCustomImpl implements ConfigQueryChartRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ConfigQueryChartDto> findByChartId(Long chartId) {
        StringBuilder sql = new StringBuilder("Select ");
        sql.append("qc.id id, ");
        sql.append("qc.query_data queryData, ");
        sql.append("qc.query_max_prd_id queryMaxPrdId, ");
        sql.append("qc.default_value defaultValue, ");
        sql.append("qc.status status, ");
        sql.append("qc.description description, ");
        sql.append("qc.update_time updateTime, ");
        sql.append("qc.update_user updateUser ");
        sql.append("from config_chart c  ");
        sql.append("inner join config_chart_item i on c.id = i.CHART_ID and i.STATUS = 1 ");
        sql.append("inner join config_query_chart qc on i.QUERY_ID  = qc.id and qc.STATUS = 1 ");
        sql.append("where c.id = :chartId ");

        Query query = em.createNativeQuery(sql.toString(), "getCfQueryChartByChartId");
        query.setParameter("chartId", chartId);

        return query.getResultList();
    }
}
