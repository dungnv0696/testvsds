package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.ServicesMapDeptDto;
import com.lifesup.gbtd.repository.ServicesMapDeptRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.StringJoiner;

public class ServicesMapDeptRepositoryCustomImpl implements ServicesMapDeptRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
//    select * from services_map_dept a, users b
//    where a.dept_code = b.dept_code and b.username = 'user đăng nhập'
//    and a.SERVICE_ID = 'ID bản ghi duoc chon'
//    and b.level_user in ('1','2');

    @Override
    public List<ServicesMapDeptDto> findWithServiceIds(List<Long> serviceIds, String username) {
        StringBuilder sql = new StringBuilder("SELECT a.ID id, " +
                "a.SERVICE_ID serviceId, a.DEPT_ID deptId " +
                "FROM services_map_dept a, users b " +
                "WHERE a.dept_id = b.dept_id " +
                "AND b.username = :username ");
        if (serviceIds.size() != 0) {
            sql.append("and a.SERVICE_ID IN ( ");
            StringJoiner strJoiner = new StringJoiner(",");
            serviceIds.forEach(id -> {
                strJoiner.add(id.toString());
            });

            sql.append(strJoiner.toString());
            sql.append(" )");
        }

        Query query = em.createNativeQuery(sql.toString(), "findWithServiceIdsMapping");
        query.setParameter("username", username);

        return query.getResultList();
    }
}
