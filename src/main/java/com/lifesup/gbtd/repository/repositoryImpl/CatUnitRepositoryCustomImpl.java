package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.CatUnitDto;
import com.lifesup.gbtd.dto.object.CatUnitRateDto;
import com.lifesup.gbtd.model.CatUnitEntity;
import com.lifesup.gbtd.model.CatUnitEntity_;
import com.lifesup.gbtd.model.ServiceGBTDEntity;
import com.lifesup.gbtd.repository.CatUnitRepositoryCustom;
import com.lifesup.gbtd.util.Const;
import com.lifesup.gbtd.util.DataUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.help.internal.criteria.Criteria;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class CatUnitRepositoryCustomImpl implements CatUnitRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<CatUnitRateDto> findConverter(Long before, List<Long> after) {
        String sql = "SELECT " +
                "r.id id, " +
                "r.UNIT_ID_AFTER unitIdBefore, " +
                "r.RATE rate, " +
                "r.UNIT_ID_BEFORE unitIdAfter " +
                "FROM " +
                "cat_unit_rate r " +
                "WHERE " +
                "STATUS = 1 " +
                "AND ( r.UNIT_ID_BEFORE = :before AND r.UNIT_ID_AFTER IN (:after )) " +
                "OR ( r.UNIT_ID_BEFORE IN (:after ) AND r.UNIT_ID_AFTER = :before ) ";
        Query query = em.createNativeQuery(sql, "catUnit.converter")
                .setParameter("before", before)
                .setParameter("after", after);
        return query.getResultList();
    }

    @Override
    public List<CatUnitEntity> findWithParam(CatUnitDto dto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CatUnitEntity> criteriaQuery = cb.createQuery(CatUnitEntity.class);
        Root<CatUnitEntity> root = criteriaQuery.from(CatUnitEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(CatUnitEntity_.STATUS), null != dto.getStatus()
                ? dto.getStatus()
                : Const.STATUS.ACTIVE));
        if (null != dto.getTypeUnit()) {
            predicates.add(cb.equal(root.get(CatUnitEntity_.TYPE_UNIT), dto.getTypeUnit()));
        } else if (!DataUtil.isNullOrEmpty(dto.getIds())) {
            predicates.add(root.get(CatUnitEntity_.TYPE_UNIT).in(dto.getIds()));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[] {}));
        return em.createQuery(criteriaQuery).getResultList();
    }
}
