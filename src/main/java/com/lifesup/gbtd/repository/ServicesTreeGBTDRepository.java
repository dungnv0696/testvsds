package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ServicesTreeGBTDEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicesTreeGBTDRepository extends JpaRepository<ServicesTreeGBTDEntity, Long>, ServiceGBTDRepositoryCustom {
    void deleteByServiceId(Long serviceId);

    int countByParentServiceIdAndServiceIdAndDeptId(Long parentServiceId, Long serviceId, Long deptId);

    ServicesTreeGBTDEntity findByParentServiceIdAndServiceIdAndDeptId(Long parentServiceId, Long serviceId, Long deptId);
}
