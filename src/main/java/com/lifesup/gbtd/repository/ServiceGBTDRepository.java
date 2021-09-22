package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ServiceGBTDEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceGBTDRepository extends JpaRepository<ServiceGBTDEntity, Long>, ServiceGBTDRepositoryCustom {
    List<ServiceGBTDEntity> findByServiceId(Long serviceId);

    List<ServiceGBTDEntity> findByServiceIdAndStatus(Long serviceId, Long status);

    void deleteByServiceId(Long serviceId);

    @Query(value = "Select * from  SERVICE_GBTD  sg where sg.SERVICE_ID = :serviceId", nativeQuery = true)
    List<ServiceGBTDEntity> findByServiceIdNative(@Param("serviceId") Long serviceId);

    Integer countByServiceId(Long serviceId);

    List<ServiceGBTDEntity> findByServiceIdIn(List<Long> serviceIdList);

    Optional<ServiceGBTDEntity> findFirstByServiceIdAndStatus(Long serviceId, Long status);
}
