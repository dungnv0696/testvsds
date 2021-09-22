package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.DashboardParamTreeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DashboardParamTreeRepository extends JpaRepository<DashboardParamTreeEntity, Long>, DashboardParamTreeRepositoryCustom {
    List<DashboardParamTreeEntity> findByDeptIdAndStatus(Long deptId, Long status);
    List<DashboardParamTreeEntity> findByParentDeptId(Long parentDeptId);
    List<DashboardParamTreeEntity> findByDeptIdOrParentDeptId(Long deptId, Long parentDeptId);
    List<DashboardParamTreeEntity> findByTypeParamAndParentDeptId(String typeParam, Long parentDeptId);
    List<DashboardParamTreeEntity> findByTypeParamAndDeptIdAndStatus(String typeParam, Long deptId, Long status);
    List<DashboardParamTreeEntity> findByTypeParamAndParentDeptIdAndDeptId(String typeParam, Long parentDeptId,Long deptId);
}