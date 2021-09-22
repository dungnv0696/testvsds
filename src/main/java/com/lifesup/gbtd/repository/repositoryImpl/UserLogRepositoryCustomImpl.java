package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.ConfigProfileDto;
import com.lifesup.gbtd.dto.object.UserLogDto;
import com.lifesup.gbtd.repository.UserLogRepositoryCustom;
import com.lifesup.gbtd.util.Const;
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
import java.util.Map;

public class UserLogRepositoryCustomImpl implements UserLogRepositoryCustom {

    public static final String PRD_ID_TO = "prdIdTo";

    @PersistenceContext
    private EntityManager em;

    @Override
    public Long getPercentUserActive(Long prdId) {
        Long startTime = (prdId / 100) * 100;
        Long endTime = startTime + 31;

        StringBuilder sql = new StringBuilder("select count(distinct(user_id)) ");
        sql.append("from user_log ");
        sql.append("where endpoint_code = 'LOGIN' ");
        sql.append("and prd_id between :startTime and :endTime ");

        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);

        return ((BigDecimal) query.getSingleResult()).longValue();
    }

    @Override
    public Long getNumberOfUser() {
        StringBuilder sql = new StringBuilder("SELECT count(DISTINCT(USER_ID)) FROM USER_LOG u ");
        sql.append("WHERE ( SELECT count(*) FROM USER_LOG WHERE ENDPOINT_CODE = 'LOGIN' ");
        sql.append("AND USER_ID = u.USER_ID) > (SELECT count(*) FROM USER_LOG WHERE ENDPOINT_CODE = 'LOGOUT' ");
        sql.append("AND USER_ID = u.USER_ID) AND u.ENDPOINT_CODE IN ('LOGIN', 'LOGOUT') ");
        Query query = em.createNativeQuery(sql.toString());
        return ((BigDecimal) query.getSingleResult()).longValue();
    }

    @Override
    public Long getNumberOfUserLK(Long prdId) {
        StringBuilder sql = new StringBuilder("SELECT count(DISTINCT(USER_ID))FROM USER_LOG ");
        sql.append("WHERE ENDPOINT_CODE = 'LOGIN' ");
        sql.append("AND PRD_ID = :prdId ");
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("prdId", prdId);
        return ((BigDecimal) query.getSingleResult()).longValue();
    }

    @Override
    public Page<UserLogDto> searchPersonal(UserLogDto dto, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("Select ul.USER_ID as userId, ul.ENDPOINT_CODE as endpointCode, ul.title , ");
        sql.append("count(ul.ENDPOINT_CODE) as usedNumber, ");
        sql.append("u.EMAIL as email, ");
        sql.append("u.POSITION_NAME as positionName, ");
        sql.append("cd.Name as deptName, ");
        sql.append("u.Name as username ");
        sql.append("from USER_LOG ul ");
        sql.append("inner join users u on ul.USER_ID = u.ID ");
        sql.append("inner join CAT_DEPARTMENT cd on u.DEPT_ID = cd.ID ");
        sql.append("where 1 = 1 ");

        if (null != dto.getUserId()) {
            sql.append("and ul.user_id = :userId ");
            params.put("userId", dto.getUserId());
        }

        sql = addPrdIdFromAndTo(sql, dto, params);

        sql.append("GROUP BY ul.ENDPOINT_CODE, ul.USER_ID, u.EMAIL, u.POSITION_NAME, cd.Name, u.NAME, ul.title ");

        return querySqlSearchBy(sql, pageable, params, "searchPersonal");
    }

    public StringBuilder addPrdIdFromAndTo(StringBuilder sql, UserLogDto dto, HashMap<String, Object> params) {
        if (null != dto.getPrdIdFrom()) {
            sql.append("and ul.prd_id >= :prdIdFrom ");
            params.put("prdIdFrom", dto.getPrdIdFrom());
        }

        if (null != dto.getPrdIdTo()) {
            sql.append("and ul.prd_id <= :prdIdTo ");
            params.put(PRD_ID_TO, dto.getPrdIdTo());
        }
        return sql;
    }

    @Override
    public Page<UserLogDto> searchDept(UserLogDto dto, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("Select ");
        sql.append(" ul.USER_ID AS userId, ");
        sql.append("null AS title, ");
        sql.append("u.EMAIL as email, ");
        sql.append("u.POSITION_NAME as positionName, ");
        sql.append("cd.Name as deptName, ");
        sql.append("(SELECT count(*) FROM USER_LOG WHERE USER_ID = ul.USER_ID AND ENDPOINT_CODE = 'LOGIN') AS loginNumber, ");
        sql.append("(SELECT count(*) FROM USER_LOG WHERE USER_ID = ul.USER_ID AND ENDPOINT_CODE = 'Cau hinh dashboard') as configDashboardNumber, ");
        sql.append("(SELECT count(*) FROM USER_LOG WHERE USER_ID = ul.USER_ID AND ENDPOINT_CODE = 'Cau hinh report dong') as configReportNumber ");
        sql.append("from USER_LOG ul ");
        sql.append("inner join users u on ul.USER_ID = u.ID ");
        sql.append("inner join CAT_DEPARTMENT cd on u.DEPT_ID = cd.ID ");
        sql.append("where 1 = 1 ");

        if (null != dto.getDeptIds()) {
            sql.append("and u.dept_id in (:deptId) ");
            params.put("deptId", dto.getDeptIds());
        }

        sql = addPrdIdFromAndTo(sql, dto, params);

        sql.append("GROUP BY ul.USER_ID,u.EMAIL,u.POSITION_NAME, cd.Name ");

        return querySqlSearchBy(sql, pageable, params, "searchDept");
    }

    public Page<UserLogDto> querySqlSearchBy(StringBuilder sql, Pageable pageable, HashMap<String, Object> params, String mapDto) {
        String sqlCount = "SELECT COUNT(*) FROM ( " + sql.toString() + " )";
        Query queryCount = em.createNativeQuery(sqlCount);
        JpaUtil.setQueryParams(queryCount, params);

        Query query = em.createNativeQuery(sql.toString(), mapDto)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        JpaUtil.setQueryParams(query, params);

        List<UserLogDto> userLogDtos = query.getResultList();
        Long count = ((BigDecimal) queryCount.getSingleResult()).longValue();

        return new PageImpl<>(userLogDtos, pageable, count);
    }

    @Override
    public Page<UserLogDto> searchMenu(UserLogDto dto, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("Select distinct(ul.ENDPOINT_CODE) as endpointCode, ul.title title, ");
        sql.append("(select count(distinct(USER_ID)) from USER_LOG where ENDPOINT_CODE = ul.ENDPOINT_CODE");
        if (null != dto.getPrdIdFrom()) {
            sql.append(" and prd_id >= :prdIdFrom ");
        }
        if (null != dto.getPrdIdTo()) {
            sql.append(" and prd_id <= :prdIdTo ");
        }
        sql.append(" ) as userNumber, ");
        sql.append("(select count(*) from USER_LOG where ENDPOINT_CODE = ul.ENDPOINT_CODE ");
        if (null != dto.getPrdIdFrom()) {
            sql.append(" and prd_id >= :prdIdFrom ");
        }
        if (null != dto.getPrdIdTo()) {
            sql.append(" and prd_id <= :prdIdTo ");
        }
        sql.append(" ) as accessNumber ");
        sql.append("from USER_LOG ul ");
        sql.append("where 1 = 1 ");

        if (null != dto.getPrdIdFrom()) {
            sql.append("and ul.prd_id >= :prdIdFrom ");
            params.put("prdIdFrom", dto.getPrdIdFrom());
        }

        if (null != dto.getPrdIdTo()) {
            sql.append("and ul.prd_id <= :prdIdTo  ");
            params.put(PRD_ID_TO, dto.getPrdIdTo());
        }

        String sqlCount = "SELECT COUNT(*) FROM ( " + sql.toString() + " )";
        Query queryCount = em.createNativeQuery(sqlCount);
        JpaUtil.setQueryParams(queryCount, params);

        Query query = em.createNativeQuery(sql.toString(), "searchMenu")
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        JpaUtil.setQueryParams(query, params);

        List<UserLogDto> userLogDtos = query.getResultList();
        Long count = ((BigDecimal) queryCount.getSingleResult()).longValue();

        return new PageImpl<>(userLogDtos, pageable, count);
    }

    @Override
    public Map<String, Object> searchTop(UserLogDto dto) {
        HashMap<String, Object> params = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT title title, ENDPOINT_CODE endpointCode, count (*) AS total ");
        sql.append("from user_log ");
        sql.append("where 1 = 1 ");

        if (null != dto.getPrdIdFrom()) {
            sql.append("and prd_id >= :prdIdFrom ");
            params.put("prdIdFrom", dto.getPrdIdFrom());
        }

        if (null != dto.getPrdIdTo()) {
            sql.append("and prd_id <= :prdIdTo ");
            params.put(PRD_ID_TO, dto.getPrdIdTo());
        }
        sql.append("and ENDPOINT_CODE not in ('LOGIN', 'LOGOUT') ");
        sql.append("group by endpoint_code, title ");

        String sql1 = sql.toString() + "order by total desc ";
        String sql2 = sql.toString() + "order by total asc ";

        Query query1 = em.createNativeQuery(sql1, "searchTop");
        query1.setFirstResult(0);
        query1.setMaxResults(5);

        JpaUtil.setQueryParams(query1, params);
        Query query2 = em.createNativeQuery(sql2, "searchTop");
        query2.setFirstResult(0);
        query2.setMaxResults(5);
        JpaUtil.setQueryParams(query2, params);

        map.put("best", query1.getResultList());
        map.put("bad", query2.getResultList());

        return map;
    }

    @Override
    public Map<String, Object> getLoginLine(Long prdIdCurrent) {
        Map<String, Object> map = new HashMap<>();

        StringBuilder sql = new StringBuilder("SELECT title title, PRD_ID prdId, ENDPOINT_CODE endpointCode, count (DISTINCT USER_ID) AS total ");
        sql.append("from user_log ");
        sql.append("where endpoint_code = 'LOGIN' ");
        sql.append("and prd_id >= :prdIdFrom ");
        sql.append("and prd_id <= :prdIdTo ");
        sql.append("GROUP BY ENDPOINT_CODE, PRD_ID, title ");
        sql.append("ORDER BY PRD_ID asc ");

        Long prdIdBefore = Const.calculatorPrdIdBefore(prdIdCurrent);

        Query query1 = em.createNativeQuery(sql.toString(), "getLoginLine");
        query1.setParameter("prdIdFrom", prdIdCurrent);
        query1.setParameter(PRD_ID_TO, prdIdCurrent + 30);
        Query query2 = em.createNativeQuery(sql.toString(), "getLoginLine");
        query2.setParameter("prdIdFrom", prdIdBefore);
        query2.setParameter(PRD_ID_TO, prdIdBefore + 30);

        map.put("curMonth", query1.getResultList());
        map.put("befMonth", query2.getResultList());
        return map;
    }

    @Override
    public Map<String, Object> getLoginLineLK(Long prdIdCurrent) {
        Map<String, Object> map = new HashMap<>();

        Long prdIdBefore = Const.calculatorPrdIdBefore(prdIdCurrent);
        StringBuilder sql = new StringBuilder("(SELECT title title, PRD_ID , ENDPOINT_CODE , count (DISTINCT USER_ID) AS total ");
        sql.append("from user_log ");
        sql.append("where endpoint_code = 'LOGIN' ");
        sql.append("and prd_id >= :prdIdFrom ");
        sql.append("and prd_id <= :prdIdTo ");
        sql.append("GROUP BY ENDPOINT_CODE, PRD_ID, title ");
        sql.append("ORDER BY PRD_ID asc ) table_t ");

        StringBuilder sql1 = new StringBuilder("SELECT table_t.PRD_ID prdId, table_t.total total,table_t.title title, ");
        sql1.append("SUM(total) OVER(ORDER BY PRD_ID ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS lk ");
        sql1.append("from ");
        sql1.append(sql);
        sql1.append("ORDER BY table_t.PRD_ID ");

        Query query1 = em.createNativeQuery(sql1.toString(), "getLoginLineLK");
        query1.setParameter("prdIdFrom", prdIdCurrent);
        query1.setParameter(PRD_ID_TO, prdIdCurrent + 30);
        Query query2 = em.createNativeQuery(sql1.toString(), "getLoginLineLK");
        query2.setParameter("prdIdFrom", prdIdBefore);
        query2.setParameter(PRD_ID_TO, prdIdBefore + 30);
        map.put("curMonth", query1.getResultList());
        map.put("befMonth", query2.getResultList());
        return map;
    }
}
