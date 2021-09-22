package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.model.ConfigMenuItemEntity;
import com.lifesup.gbtd.model.ConfigMenuItemEntity_;
import com.lifesup.gbtd.repository.ConfigMenuItemRepositoryCustom;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigMenuItemRepositoryCustomImpl implements ConfigMenuItemRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ConfigMenuItemEntity> findAll(String keyword, Long[] menuIds, Long isDefault, Long status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ConfigMenuItemEntity> criteria = cb.createQuery(ConfigMenuItemEntity.class);
        Root<ConfigMenuItemEntity> root = criteria.from(ConfigMenuItemEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotEmpty(keyword)) {
            keyword = DataUtil.makeLikeParam(keyword);
            predicates.add(
                    cb.like(cb.lower(root.get("menuItemName")), keyword, Const.DEFAULT_ESCAPE_CHAR)
            );
        }
        if (!DataUtil.isNullOrEmpty(menuIds)) {
            Expression<Long> inExpression = root.get("menuId");
            predicates.add(inExpression.in(menuIds));
        }
        if (isDefault != null) {
            predicates.add(cb.equal(root.get("isDefault"), isDefault));
        }

        status = status != null ? status : Const.STATUS.ACTIVE;
        predicates.add(cb.equal(root.get("status"), status));
        criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        criteria.orderBy(Arrays.asList(
                cb.asc((root.get(ConfigMenuItemEntity_.ORDER_INDEX))),
                cb.asc((root.get(ConfigMenuItemEntity_.MENU_ITEM_NAME)))
        ));

        return em.createQuery(criteria).getResultList();
    }
}
