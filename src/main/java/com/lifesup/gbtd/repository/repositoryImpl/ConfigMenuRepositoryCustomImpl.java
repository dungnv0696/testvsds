package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.ConfigMenuDto;
import com.lifesup.gbtd.dto.object.ConfigMenuItemDto;
import com.lifesup.gbtd.model.ConfigDashboardEntity;
import com.lifesup.gbtd.model.ConfigDashboardEntity_;
import com.lifesup.gbtd.model.ConfigMenuEntity;
import com.lifesup.gbtd.model.ConfigMenuEntity_;
import com.lifesup.gbtd.model.ConfigMenuItemEntity;
import com.lifesup.gbtd.model.ConfigMenuItemEntity_;
import com.lifesup.gbtd.repository.ConfigMenuRepositoryCustom;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfigMenuRepositoryCustomImpl implements ConfigMenuRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ConfigMenuItemDto> getConfigMenuItemsByMenuIdAndProfileId(Long menuId, Long profileId) {
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append("a.id id, a.menu_item_name menuItemName ");
        sql.append("from config_menu_item a ");
        sql.append("inner join config_menu b on a.menu_id = b.id and b.status = 1 ");
        sql.append("Where a.status = 1 And b.profile_id = :profileId ");
        sql.append("and a.menu_id = :menuId ");
        sql.append("Order by a.order_index, a.menu_item_name ");

        Query query = em.createNativeQuery(sql.toString(), "getConfigMenuItemsByMenuIdAndProfileId");
        query.setParameter("menuId", menuId);
        query.setParameter("profileId", profileId);
        return query.getResultList();
    }

    @Override
    public List findAllByProfileIds(Long[] profileIds) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery criteria = cb.createQuery();
        Root<ConfigMenuEntity> menuRoot = criteria.from(ConfigMenuEntity.class);
        Root<ConfigMenuItemEntity> menuItemRoot = criteria.from(ConfigMenuItemEntity.class);
        Root<ConfigDashboardEntity> dashboardRoot = criteria.from(ConfigDashboardEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(menuRoot.get(ConfigMenuEntity_.STATUS), Const.STATUS.ACTIVE));
        predicates.add(cb.equal(menuRoot.get(ConfigMenuEntity_.ID), menuItemRoot.get(ConfigMenuItemEntity_.MENU_ID)));
        predicates.add(cb.equal(menuItemRoot.get(ConfigMenuItemEntity_.STATUS), Const.STATUS.ACTIVE));
        predicates.add(cb.equal(menuItemRoot.get(ConfigMenuItemEntity_.ID), dashboardRoot.get(ConfigDashboardEntity_.MENU_ITEM_ID)));
        predicates.add(cb.equal(dashboardRoot.get(ConfigDashboardEntity_.STATUS), Const.STATUS.ACTIVE));

        if (!DataUtil.isNullOrEmpty(profileIds)) {
            Expression<Long> inExpression = dashboardRoot.get(ConfigDashboardEntity_.PROFILE_ID);
            predicates.add(inExpression.in(profileIds));
        }

        criteria.multiselect(menuRoot, menuItemRoot, dashboardRoot).distinct(true);
        criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        criteria.orderBy(cb.asc(menuRoot.get(ConfigMenuEntity_.ORDER_INDEX)), cb.asc(menuItemRoot.get(ConfigMenuItemEntity_.ORDER_INDEX)));
        List list = em.createQuery(criteria).getResultList();
        return list;
    }

    @Override
    public Page<ConfigMenuDto> getListConfigMenu(ConfigMenuDto dto, Pageable pageable) {
        HashMap<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append("cm.id, ");
        sql.append("cm.menu_name menuName, ");
        sql.append("cm.profile_id profileId, ");
        sql.append("cm.status status, ");
        sql.append("cm.description description, ");
        sql.append("cm.update_time updateTime, ");
        sql.append("cm.update_user updateUser, ");
        sql.append("cm.order_index orderIndex ");
        sql.append("From config_menu cm ");
        sql.append("Where 1 = 1 ");
        sql.append("and cm.profile_id in (:profileIds) ");

        if (StringUtils.isNotEmpty(dto.getMenuName())) {
            sql.append("and lower(cm.menu_name) like :menuName ESCAPE '&' ");
            params.put("menuName", DataUtil.makeLikeParam(dto.getMenuName()));
        }
        params.put("profileIds", dto.getProfileIds());

        String sqlCount = "SELECT COUNT(*) FROM ( " + sql.toString() + " )";
        Query queryCount = em.createNativeQuery(sqlCount);
        JpaUtil.setQueryParams(queryCount, params);

        Query query = em.createNativeQuery(sql.toString(), "getConfigMenus")
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        JpaUtil.setQueryParams(query, params);

        List<ConfigMenuDto> configMenuDtos = query.getResultList();
        Long count = ((BigDecimal) queryCount.getSingleResult()).longValue();

        return new PageImpl<>(configMenuDtos, pageable, count);
    }

    private String createSubQuery() {
        StringBuilder subSql = new StringBuilder("SELECT DISTINCT ");
        subSql.append("a.ID as id, ");
        subSql.append("a.profile_name as profileName, ");
        subSql.append("b.role_code as roleCode ");


        return subSql.toString();
    }

    @Override
    public List<ConfigMenuEntity> getAllConfigMenu(ConfigMenuDto dto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ConfigMenuEntity> criteria = cb.createQuery(ConfigMenuEntity.class);
        Root<ConfigMenuEntity> menuRoot = criteria.from(ConfigMenuEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if (null != dto.getProfileId()) {
            predicates.add(cb.equal(menuRoot.get(ConfigMenuEntity_.PROFILE_ID), dto.getProfileId()));
        } else if (!DataUtil.isNullOrEmpty(dto.getProfileIds())) {
            predicates.add(menuRoot.get(ConfigMenuEntity_.PROFILE_ID).in(dto.getProfileIds()));
        }
        criteria.where(predicates.toArray(new Predicate[] {}));
        criteria.orderBy(
                cb.asc(menuRoot.get(ConfigMenuEntity_.ORDER_INDEX)),
                cb.asc(menuRoot.get(ConfigMenuEntity_.MENU_NAME))
        );
        Query query = em.createQuery(criteria);
        return query.getResultList();
    }
}
