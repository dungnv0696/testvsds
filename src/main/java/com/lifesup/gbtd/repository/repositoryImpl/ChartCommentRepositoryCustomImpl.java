package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.ChartCommentDto;
import com.lifesup.gbtd.repository.ChartCommentRepositoryCustom;
import com.lifesup.gbtd.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChartCommentRepositoryCustomImpl implements ChartCommentRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ChartCommentDto> doSearch(ChartCommentDto dto) {
        HashMap<String, Object> params = new HashMap<>();
        String sql = "SELECT * " +
                " FROM ( SELECT cc.ID as id, " +
                "cc.CHART_ID as chartId," +
                "cc.USER_NAME as userName, " +
                "cc.DATE_TIME as dateTime, " +
                "cc.CONTENT as content  " +
                "FROM CHART_COMMENT cc WHERE cc.CHART_ID = :chartId ";

        params.put("chartId", dto.getChartId());
        if (Objects.nonNull(dto.getId())) {
            sql += "AND cc.ID < :id ";
            params.put("id", dto.getId());
        }
        sql += " ORDER BY cc.ID DESC) cc where rownum <= 5 ";

        Query query = em.createNativeQuery(sql, "chartComment.tableDesc");
        JpaUtil.setQueryParams(query, params);
        return query.getResultList();
    }
}
