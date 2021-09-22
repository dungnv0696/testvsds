package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.dto.object.ConfigDashboardDto;
import com.lifesup.gbtd.model.ConfigDashboardEntity;
import com.lifesup.gbtd.model.ConfigDashboardEntity_;
import com.lifesup.gbtd.repository.ConfigDashboardRepositoryCustom;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfigDashboardRepositoryCustomImpl implements ConfigDashboardRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<ConfigDashboardDto> doSearch(ConfigDashboardDto dto, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        String sql = "SELECT " +
                "a.ID id, " +
                "a.DASHBOARD_NAME dashboardName, " +
                "a.DASHBOARD_TYPE dashboardType, " +
                "a.ORDER_INDEX orderIndex, " +
                "a.PROFILE_ID profileId, " +
                "a.MENU_ITEM_ID menuItemId, " +
                "a.STATUS status, " +
                "a.DESCRIPTION description, " +
                "a.UPDATE_TIME updateTime, " +
                "a.UPDATE_USER updateUser " +
                "FROM CONFIG_DASHBOARD a " +
                "LEFT JOIN CONFIG_MENU_ITEM b ON a.MENU_ITEM_ID = b.ID " +
                "WHERE 1=1 ";

        if (StringUtils.isNotEmpty(dto.getKeyword())) {
            sql += "AND LOWER(a.DASHBOARD_NAME) LIKE :keyword ESCAPE '" + Const.DEFAULT_ESCAPE_CHAR + "' ";
            params.put("keyword", DataUtil.makeLikeParam(dto.getKeyword()));
        }
        if (!DataUtil.isNullOrEmpty(dto.getProfileIds())) {
            sql += "AND a.PROFILE_ID in (:profileIds) ";
            params.put("profileIds", Arrays.asList(dto.getProfileIds()));
        }
        if (!DataUtil.isNullOrEmpty(dto.getMenuItemIds())) {
            sql += "AND a.MENU_ITEM_ID in (:menuItemIds) ";
            params.put("menuItemIds", Arrays.asList(dto.getMenuItemIds()));
        }
        if (!DataUtil.isNullOrEmpty(dto.getMenuIds())) {
            sql += "AND b.MENU_ID in (:menuIds) ";
            params.put("menuIds", Arrays.asList(dto.getMenuIds()));
        }
        if (Objects.nonNull(dto.getDashboardType())) {
            sql += "AND a.DASHBOARD_TYPE = :dashboardType ";
            params.put("dashboardType", dto.getDashboardType());
        }
        if (Objects.nonNull(dto.getStatus())) {
            sql += "AND a.STATUS = :status ";
            params.put("status", dto.getStatus());
        } else {
            sql += "AND a.STATUS = 1 ";
        }
        if (pageable.getPageSize() > 50) {
            sql += "ORDER BY a.DASHBOARD_NAME, a.ORDER_INDEX desc ";
        } else {
            sql += "ORDER BY a.ID desc";
        }

        Query query = em.createNativeQuery(sql, "configDashboard.doSearch");
        Query queryCount = em.createNativeQuery(JpaUtil.toCountQuery(sql));
        return JpaUtil.getPageableResult(query, queryCount, params, pageable);
    }

    @Override
    @Transactional
    public List<ConfigDashboardEntity> findAll(String keyword, Long[] profileIds, Long[] menuIds, Long[] menuItemIds, Long isDefault, Long status) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ConfigDashboardEntity> criteria = cb.createQuery(ConfigDashboardEntity.class);
        Root<ConfigDashboardEntity> root = criteria.from(ConfigDashboardEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotEmpty(keyword)) {
            keyword = DataUtil.makeLikeParam(keyword);
            predicates.add(cb.like(cb.lower(root.get(ConfigDashboardEntity_.DASHBOARD_NAME)), keyword, Const.DEFAULT_ESCAPE_CHAR));
        }
        if (!DataUtil.isNullOrEmpty(profileIds)) {
            Expression<Long> inExpression = root.get(ConfigDashboardEntity_.PROFILE_ID);
            predicates.add(inExpression.in(profileIds));
        }
        if (!DataUtil.isNullOrEmpty(menuItemIds)) {
            Expression<Long> inExpression = root.get(ConfigDashboardEntity_.MENU_ITEM_ID);
            predicates.add(inExpression.in(menuItemIds));
        }

        status = status != null ? status : Const.STATUS.ACTIVE;
        predicates.add(cb.equal(root.get(ConfigDashboardEntity_.STATUS), status));
        criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        Expression exp = cb.coalesce(root.get(ConfigDashboardEntity_.ORDER_INDEX), Long.MAX_VALUE);
        criteria.orderBy(cb.asc(exp), cb.desc(root.get(ConfigDashboardEntity_.ID)));
        List<ConfigDashboardEntity> rs = em.createQuery(criteria).getResultList();
        return rs;
    }

    @Override
    public List<CatItemDto> getTimeTypeByServiceId(Long[] serviceIds) {
        String sql = "select DISTINCT " +
                "a.ITEM_ID itemId, " +
                "a.ITEM_CODE itemCode, " +
                "a.ITEM_NAME itemName, " +
                "a.ITEM_VALUE itemValue, " +
                "a.CATEGORY_ID categoryId, " +
                "a.CATEGORY_CODE categoryCode, " +
                "a.POSITION position, " +
                "a.DESCRIPTION description, " +
                "a.EDITABLE editable, " +
                "a.PARENT_ITEM_ID parentItemId, " +
                "a.STATUS status, " +
                "a.UPDATE_TIME updateTime, " +
                "a.UPDATE_USER updateUser, " +
                "null parentName " +
                "from (SELECT ITEM_ID,ITEM_CODE,ITEM_NAME,ITEM_VALUE,CATEGORY_ID,CATEGORY_CODE,POSITION," +
                "DESCRIPTION,EDITABLE,PARENT_ITEM_ID,STATUS,UPDATE_TIME,UPDATE_USER " +
                "FROM CAT_ITEM WHERE CATEGORY_CODE = 'TIME_TYPE') a " +
                "LEFT JOIN RPT_DATA_NEWEST rpt ON rpt.TIME_TYPE = a.ITEM_VALUE " +
                "WHERE rpt.SERVICE_ID in (:serviceIds) " +
                "And a.STATUS = :status";

        Query query = em.createNativeQuery(sql, "findCatItemsMapping")
                .setParameter("serviceIds", Arrays.asList(serviceIds))
                .setParameter("status", Const.STATUS.ACTIVE);
        return query.getResultList();
    }
}
