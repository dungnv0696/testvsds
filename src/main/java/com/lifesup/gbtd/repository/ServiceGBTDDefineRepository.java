package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ServiceGBTDDefineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceGBTDDefineRepository extends JpaRepository<ServiceGBTDDefineEntity, Long>, ServiceGBTDDefineRepositoryCustom {
    void deleteByServiceId(Long serviceId);

    ServiceGBTDDefineEntity findAllByServiceIdAndDeptIdAndTimeType(Long serviceId, Long deptId, Long timeType);
    //List<ServiceGBTDDefineEntity> findByServiceId(Long serviceId);
    void deleteByServiceIdAndDeptIdAndTimeType(Long serviceId, Long deptId, Long timeType);
    void deleteByServiceIdAndDeptId(Long serviceId, Long deptId);
    List<ServiceGBTDDefineEntity> findByServiceIdAndDeptId(Long serviceId, Long deptId);
    List<ServiceGBTDDefineEntity> findByServiceId(Long serviceId);
}
