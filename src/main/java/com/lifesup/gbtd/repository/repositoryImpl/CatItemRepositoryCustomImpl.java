package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.model.CatItemEntity;
import com.lifesup.gbtd.model.CatItemEntity_;
import com.lifesup.gbtd.model.ConfigChartDefaultEntity;
import com.lifesup.gbtd.model.ConfigChartDefaultEntity_;
import com.lifesup.gbtd.model.ConfigChartRoleEntity;
import com.lifesup.gbtd.model.ConfigChartRoleEntity_;
import com.lifesup.gbtd.repository.CatItemRepositoryCustom;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import com.lifesup.gbtd.util.JpaUtil;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Transactional
public class CatItemRepositoryCustomImpl implements CatItemRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<CatItemDto> getCatItems() {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT ci.ITEM_ID AS \"itemId\"," +
                "ci.CATEGORY_CODE AS \"categoryCode\"," +
                "ci.ITEM_NAME AS \"itemName\"" +
                "FROM CAT_ITEM ci " +
                "INNER JOIN " +
                "CAT_ITEM chi ON ci.ITEM_ID = chi.PARENT_ITEM_ID " +
                "WHERE ci.STATUS = 1 " +
                "ORDER BY ci.CATEGORY_CODE, ci.ITEM_NAME");
        Query query = em.createNativeQuery(sql.toString(), "getCatItemsMapping");
        return query.getResultList();
    }

    @Override
    public List<CatItemDto> findCatItems(CatItemDto dto) {
        HashMap<String, Object> params = new HashMap<>();
//        Objects.nonNull(dto.getPage()) ? dto.getPage() : 1;
//        Objects.nonNull(dto.getPageSize()) ? dto.getPageSize() : 10;
        StringBuilder sql = new StringBuilder(" SELECT ci.ITEM_ID itemId, " +
                "ci.ITEM_CODE itemCode, " +
                "ci.ITEM_NAME itemName, " +
                "ci.ITEM_VALUE itemValue, " +
                "ci.CATEGORY_ID categoryId, " +
                "ci.CATEGORY_CODE categoryCode, " +
                "ci.POSITION position, " +
                "ci.DESCRIPTION description, " +
                "ci.EDITABLE editable, " +
                "ci.PARENT_ITEM_ID parentItemId, " +
                "ci.STATUS status, " +
                "ci.UPDATE_TIME updateTime, " +
                "ci.UPDATE_USER updateUser, " +
                " par.ITEM_NAME parentName" +
                " FROM CAT_ITEM ci " +
                " LEFT JOIN " +
                " CAT_ITEM par ON par.ITEM_ID = ci.PARENT_ITEM_ID " +
                " WHERE ci.STATUS = :status ");
//        if (Objects.nonNull(dto.getCategoryIds()) && !dto.getCategoryIds().isEmpty()) {
//            sql.append("AND ci.CATEGORY_ID in (:categoryIds) ");
//            params.put("categoryIds", dto.getCategoryIds());
//        }
        if (Objects.nonNull(dto.getCategoryId())) {
            sql.append("AND ci.CATEGORY_ID = :categoryId ");
            params.put("categoryId", dto.getCategoryId());
        }
        if (StringUtils.isNotEmpty(dto.getParentName())) {
            sql.append("AND par.ITEM_NAME = :parentName ");
            params.put("parentName", dto.getParentName());
        }
        if (StringUtils.isNotEmpty(dto.getItemName())) {
            sql.append("AND LOWER(ci.ITEM_NAME) like :itemName ESCAPE '&' ");
            params.put("itemName", DataUtil.makeLikeParam(dto.getItemName()));
        }
        if (StringUtils.isNotEmpty(dto.getItemCode())) {
            sql.append("AND ci.ITEM_CODE like UPPER(:itemCode) ESCAPE '&' ");
            params.put("itemCode", DataUtil.makeLikeParam(dto.getItemCode()));
        }
        if (StringUtils.isNotEmpty(dto.getItemValue())) {
            sql.append("AND LOWER(ci.ITEM_VALUE) like :itemValue ESCAPE '&' ");
            params.put("itemValue", DataUtil.makeLikeParam(dto.getItemValue()));
        }

        sql.append("ORDER BY ci.CATEGORY_CODE, par.ITEM_NAME, ci.position, ci.ITEM_NAME ");

        // Count queried record
        String sqlCount = "SELECT COUNT(*) FROM ( " + sql.toString() + " )";
        Query queryCount = em.createNativeQuery(sqlCount);
        JpaUtil.setQueryParams(queryCount, params);

        // Query limit offset
        Query query = em.createNativeQuery(sql.toString(), "findCatItemsMapping");
        if (Objects.nonNull(dto.getStatus())) {
            query.setParameter("status", dto.getStatus().longValue());
            queryCount.setParameter("status", dto.getStatus().longValue());
        } else {
            query.setParameter("status", Const.STATUS.ACTIVE);
            queryCount.setParameter("status", Const.STATUS.ACTIVE);
        }
        JpaUtil.setQueryParams(query, params);

        JpaUtil.setPaging(dto, query, queryCount);
        return query.getResultList();
    }

    @Override
    public int deleteCatItemById(Long id) {
        return em.createQuery("DELETE FROM CatItemEntity ci where ci.itemId = :itemId")
                .setParameter("itemId", id)
                .executeUpdate();
    }

    @Override
    public boolean validateKeysAddAndUpdate(String categoryCode, String itemCode, Long parentItemId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) " +
                "FROM CAT_ITEM ci " +
                "WHERE ci.CATEGORY_CODE = :categoryCode " +
                "AND ci.ITEM_CODE = :itemCode "
        );
        if (Objects.nonNull(parentItemId)) {
            sql.append("AND ci.PARENT_ITEM_ID = :parentItemId ");
        }

        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("categoryCode", categoryCode);
        query.setParameter("itemCode", itemCode);
        if (Objects.nonNull(parentItemId)) {
            query.setParameter("parentItemId", parentItemId);
        }

        return ((Number) query.getSingleResult()).longValue() == 0;
    }

    @Override
    public List<String> findByCatCodeAndItemCode(String categoryCode, String itemCode) {
        String sql = "SELECT ITEM_NAME itemName FROM CAT_ITEM " +
                "WHERE CATEGORY_CODE = :categoryCode " +
                "AND ITEM_CODE = :itemCode ";

        Query query = em.createNativeQuery(sql, "catItem.getServiceSource")
                .setParameter("categoryCode", categoryCode)
                .setParameter("itemCode", itemCode);
        return query.getResultList();
    }

    @Override
    public CatItemDto findByCatCodeAndItemCodeAndParentItemId(String categoryCode, String itemCode, Long parentItemId) {
        StringBuilder sql = new StringBuilder("SELECT ci.ITEM_ID itemId " +
                "FROM CAT_ITEM ci " +
                "WHERE ci.CATEGORY_CODE = :categoryCode " +
                "AND ci.ITEM_CODE = :itemCode "
        );
        if (Objects.nonNull(parentItemId)) {
            sql.append("AND ci.PARENT_ITEM_ID = :parentItemId ");
        }

        Query query = em.createNativeQuery(sql.toString(), "findByCatCodeAndItemCodeAndParentItemId");
        query.setParameter("categoryCode", categoryCode);
        query.setParameter("itemCode", itemCode);
        if (Objects.nonNull(parentItemId)) {
            query.setParameter("parentItemId", parentItemId);
        }

        List list = query.getResultList();
        if (!list.isEmpty()) return (CatItemDto) list.get(0);
        return null;
    }

    @Override
    public List<CatItemEntity> findAll(CatItemDto dto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CatItemEntity> criteria = cb.createQuery(CatItemEntity.class);
        Root<CatItemEntity> root = criteria.from(CatItemEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(dto.getItemId())) {
            Predicate predicate = cb.equal(root.get(CatItemEntity_.ITEM_ID), dto.getItemId());
            predicates.add(predicate);
        }

        if (StringUtils.isNotEmpty(dto.getItemCode())) {
            Predicate predicate = cb.equal(cb.lower(root.get(CatItemEntity_.ITEM_CODE)), dto.getItemCode().toLowerCase());
            predicates.add(predicate);
        }

        if (Objects.nonNull(dto.getCategoryId())) {
            Predicate predicate = cb.equal(root.get(CatItemEntity_.CATEGORY_ID), dto.getCategoryId());
            predicates.add(predicate);
        }

        if (!DataUtil.isNullOrEmpty(dto.getCategoryCodes())) {
            if (dto.getCategoryCodes().size() == 1 && Const.CAT_ITEM_CODE.OUTPUT_SEARCH.equals(dto.getCategoryCodes().get(0))) {
                Root<ConfigChartDefaultEntity> cfgRoot = criteria.from(ConfigChartDefaultEntity.class);
                predicates.add(cb.equal(root.get(CatItemEntity_.ITEM_VALUE), cfgRoot.get(ConfigChartDefaultEntity_.TYPE_CHART)));
                predicates.add(cb.equal(cfgRoot.get(ConfigChartDefaultEntity_.STATUS), Const.STATUS.ACTIVE));
            }
            predicates.add(root.get(CatItemEntity_.CATEGORY_CODE).in(dto.getCategoryCodes()));
        }

        if (Objects.nonNull(dto.getParentItemId())) {
            Predicate predicate = cb.equal(root.get(CatItemEntity_.PARENT_ITEM_ID), dto.getParentItemId());
            predicates.add(predicate);
        }

        if (StringUtils.isNotEmpty(dto.getParentCode()) || !DataUtil.isNullOrEmpty(dto.getParentCategoryCodes()) || StringUtils.isNotEmpty(dto.getParentValue())) {
            Root<CatItemEntity> parentRoot = criteria.from(CatItemEntity.class);
            predicates.add(cb.equal(root.get(CatItemEntity_.PARENT_ITEM_ID), parentRoot.get(CatItemEntity_.ITEM_ID)));
            predicates.add(cb.equal(parentRoot.get(CatItemEntity_.STATUS), Const.STATUS.ACTIVE));

            if (StringUtils.isNotEmpty(dto.getParentCode())) {
                predicates.add(cb.equal(parentRoot.get(CatItemEntity_.ITEM_CODE), dto.getParentCode()));
            }

            if (!DataUtil.isNullOrEmpty(dto.getParentCategoryCodes())) {
                predicates.add(parentRoot.get(CatItemEntity_.CATEGORY_CODE).in(dto.getParentCategoryCodes()));
            }

            if (StringUtils.isNotEmpty(dto.getParentValue())) {
                predicates.add(cb.equal(parentRoot.get(CatItemEntity_.ITEM_VALUE), dto.getParentValue()));
            }
        }

        predicates.add(cb.equal(root.get(CatItemEntity_.STATUS), Const.STATUS.ACTIVE));
        criteria.select(root).distinct(true);
        criteria.where(cb.and(predicates.toArray(new Predicate[]{})));
        criteria.orderBy(Arrays.asList(
                cb.asc((root.get(CatItemEntity_.POSITION))),
                cb.asc((root.get(CatItemEntity_.ITEM_NAME)))
        ));

        return em.createQuery(criteria).getResultList();
    }
}
