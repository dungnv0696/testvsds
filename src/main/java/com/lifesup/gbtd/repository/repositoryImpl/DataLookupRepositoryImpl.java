package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.repository.DataLookupRepository;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Repository
@Slf4j
public class DataLookupRepositoryImpl implements DataLookupRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Long getLatestDate(Long[] serviceIds, String[] deptCodes, Long timeType) {
        String sql = "SELECT max(prd_id) TO_TIME " +
                "FROM RPT_DATA_NEWEST " +
                "WHERE service_id IN (:serviceIds) " +
                "AND obj_code IN (:deptCodes) " +
                "AND TIME_TYPE = :timeType ";
        Query query = em.createNativeQuery(sql)
                .setParameter("serviceIds", Arrays.asList(serviceIds))
                .setParameter("deptCodes", Arrays.asList(deptCodes))
                .setParameter("timeType", timeType);
        List l = query.getResultList();
        if (!DataUtil.isNullOrEmpty(l)) {
            if (null != l.get(0)) {
                return ((BigDecimal) l.get(0)).longValue();
            }
        }
        return null;
    }

    @Override
    public List<Object> executeGetDataSql(String sql, Map<String, Object> params) {
        Query query = em.createNativeQuery(sql, Tuple.class);
        JpaUtil.setQueryParams(query, params);
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        return query.getResultList();
    }
}
