package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.BiTdServicesTreeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BiTdServicesTreeRepository extends JpaRepository<BiTdServicesTreeEntity, Long> {

    List<BiTdServicesTreeEntity> findByServiceIdAndParentServiceIdAndDeptId(Long serviceId, Long parentServiceId, Long deptId);

    List<BiTdServicesTreeEntity> findByParentServiceIdAndParentDeptIdAndServiceIdAndDeptId(Long parentServiceId,
                                                                                           Long parentDeptId, Long serviceId,
                                                                                           Long deptId);

    List<BiTdServicesTreeEntity> findByParentServiceIdAndParentDeptIdAndServiceIdAndDeptIdAndTypeParam(Long parentServiceId,
                                                                                                       Long parentDeptId, Long serviceId,
                                                                                                       Long deptId, String typeParam);

    List<BiTdServicesTreeEntity> findByParentServiceIdAndParentDeptIdAndDeptIdAndTypeParam(Long parentServiceId,
                                                                                           Long parentDeptId,
                                                                                           Long deptId, String typeParam);

    void deleteByParentServiceIdAndParentDeptId(Long parentServiceId, Long parentDeptId);

    List<BiTdServicesTreeEntity> findByParentServiceIdAndParentDeptId(Long parServiceId, Long parDeptId);

    List<BiTdServicesTreeEntity> findByParentServiceId(Long parServiceId);
    List<BiTdServicesTreeEntity> findByDeptCodeAndServiceIdAndParentDeptCodeNotNull(String deptCode,Long serviceid);
}
