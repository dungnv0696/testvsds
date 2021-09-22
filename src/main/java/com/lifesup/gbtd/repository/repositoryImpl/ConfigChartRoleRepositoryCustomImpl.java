package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.ConfigChartRoleDto;
import com.lifesup.gbtd.model.ConfigChartRoleEntity;
import com.lifesup.gbtd.model.ConfigChartRoleEntity_;
import com.lifesup.gbtd.repository.ConfigChartRoleRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigChartRoleRepositoryCustomImpl implements ConfigChartRoleRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ConfigChartRoleEntity> findAll(ConfigChartRoleDto dto, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ConfigChartRoleEntity> criteria = cb.createQuery(ConfigChartRoleEntity.class);
        Root<ConfigChartRoleEntity> root = criteria.from(ConfigChartRoleEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(dto.getChartId())) {
            Predicate predicate = cb.equal(root.get(ConfigChartRoleEntity_.CHART_ID), dto.getChartId());
            predicates.add(predicate);
        }

        Sort sort = pageable.getSort();
        List<Order> orders = new ArrayList<>();
        orders.add(cb.desc(root.get(ConfigChartRoleEntity_.ID)));
        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                if (order.isDescending()) {
                    orders.add(cb.desc(root.get(order.getProperty())));
                } else {
                    orders.add(cb.asc(root.get(order.getProperty())));
                }
            }
        }
        criteria.orderBy(orders);
        criteria.where(cb.and(predicates.toArray(new Predicate[] {})));

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ConfigChartRoleEntity> rootCount = countQuery.from(ConfigChartRoleEntity.class);
        countQuery.select(cb.count(rootCount)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        List<ConfigChartRoleEntity> rs = em.createQuery(criteria).setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();

        Long count = em.createQuery(countQuery).getSingleResult();
        dto.setTotalRow(count);
        long totalPage = (count % pageable.getPageSize()) == 0 ? count / pageable.getPageSize()
                : count / pageable.getPageSize() + 1;
        dto.setTotalPage(totalPage);

        return rs;
    }
}
