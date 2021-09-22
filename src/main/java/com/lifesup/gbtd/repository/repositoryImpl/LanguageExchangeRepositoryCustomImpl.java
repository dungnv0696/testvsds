package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.LanguageExchangeDto;
import com.lifesup.gbtd.repository.LanguageExchangeRepositoryCustom;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;

public class LanguageExchangeRepositoryCustomImpl implements LanguageExchangeRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<LanguageExchangeDto> getLanguageExchanges(LanguageExchangeDto dto) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT ")
                .append("le.LEE_ID leeId, ")
                .append("le.APPLIED_BUSINESS appliedBusiness, ")
                .append("bsn.")
                .append(dto.getBusinessIdCol())
                .append(" businessId, ")
                .append("bsn.")
                .append(dto.getLeeValueCol())
                .append(" defaultValue, ")
                .append("le.LEE_LOCALE leeLocale, ")
                .append("le.lee_value leeValue ")
                .append("FROM ")
                .append(dto.getAppliedBusiness())
                .append(" bsn ")
                .append("LEFT JOIN language_exchange le ON bsn.")
                .append(dto.getBusinessIdCol())
                .append(" = le.BUSINESS_ID ")
                .append("AND le.APPLIED_BUSINESS = :appliedBusiness ")
                .append("AND le.LEE_LOCALE = :leeLocale ")
                .append("Where 1 = 1 ");

        if (Const.STATUS.ACTIVE.equals(dto.getTranslateStatus())) {
            sql.append("AND le.LEE_ID IS not null");
        } else if (Const.STATUS.DISABLED.equals(dto.getTranslateStatus())) {
            sql.append("AND le.LEE_ID IS null");
        }

        params.put("leeLocale", dto.getLeeLocale());
        params.put("appliedBusiness", dto.getAppliedBusiness());

        Query query = em.createNativeQuery(sql.toString(), "getLanguageExchangeMapping");
        JpaUtil.setQueryParams(query, params);

        return query.getResultList();
    }

    @Override
    public List checkExistData(String tableName, String column, String value) {
        StringBuilder sql = new StringBuilder("SELECT 1 ");
        sql.append("From ");
        sql.append(tableName);
        sql.append(" Where ");
        sql.append(column);
        sql.append(" = :value");

        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("value", value);

        return query.getResultList();
    }
}
