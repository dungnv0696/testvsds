package com.lifesup.gbtd.repository.repositoryImpl;

import com.lifesup.gbtd.dto.object.ServiceGBTDDefineDto;
import com.lifesup.gbtd.repository.ServiceGBTDDefineRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ServiceGBTDDefineRepositoryCustomImpl implements ServiceGBTDDefineRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ServiceGBTDDefineDto> findByServiceIdAndDeptIds(Long serviceId, Long deptId) {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT a.DEPT_ID deptId, " +
                "a.DEFINATION defination, " +
                "a.UPDATE_TIME updateTime, " +
                "a.UPDATE_USER updateUser, " +
                "d.timeTypesStr " +
                "FROM SERVICE_GBTD_DEFINE a " +
                "INNER JOIN ( SELECT s.SERVICE_ID, s.DEPT_ID, listagg(time_type, ',') " +
                "WITHIN GROUP (ORDER BY SERVICE_ID) timeTypesStr " +
                "FROM SERVICE_GBTD_DEFINE s " +
                "GROUP BY s.SERVICE_ID, s.DEPT_ID) d ON d.SERVICE_ID = a.SERVICE_ID AND a.DEPT_ID=d.DEPT_ID " +
                "AND a.SERVICE_ID = :serviceId ");
        sql.append("AND a.dept_id = :deptId ");
        Query query = em.createNativeQuery(sql.toString(), "getServiceGBTDDefineMapping");
        query.setParameter("serviceId", serviceId);
        query.setParameter("deptId", deptId);
        return query.getResultList();
    }
}
