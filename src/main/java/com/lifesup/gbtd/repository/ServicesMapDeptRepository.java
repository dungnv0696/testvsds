package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ServicesMapDeptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicesMapDeptRepository extends JpaRepository<ServicesMapDeptEntity, Long>,
        ServicesMapDeptRepositoryCustom {
    ServicesMapDeptEntity findServicesMapDeptEntityByServiceId(Long serviceId);
    int deleteByServiceId(Long serviceId);
    List<ServicesMapDeptEntity> findByServiceId(Long serviceId);
    List<ServicesMapDeptEntity> findByDeptIdAndServiceIdIsNot(Long deptId, Long serviceId);
    List<ServicesMapDeptEntity> findByDeptId(Long deptId);
    ServicesMapDeptEntity findByServiceIdAndDeptId(Long serviceId, Long deptId);
    void deleteByServiceIdAndDeptId(Long serviceId, Long deptId);
    void deleteByServiceIdAndDeptIdIn(Long serviceId, List<Long> deptId);
    List<ServicesMapDeptEntity> findByServiceIdAndDeptIdIn(Long serviceId, List<Long> deptId);
}
