package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.CatDepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CatDepartmentRepository extends JpaRepository<CatDepartmentEntity, Long>, CatDepartmentRepositoryCustom {
    @Query(value = "select cd.id from CatDepartmentEntity cd where cd.parentId in (:parents) and cd.status = :status")
    Set<Long> findByParentIdInAndStatus(Set<Long> parents, Long status);
    List<CatDepartmentEntity> findByDeptLevelNotInAndStatus(List<Long> deptLevelList, Long status);
    List<CatDepartmentEntity> findByCode(String code);
    Optional<CatDepartmentEntity> findByIdAndStatus(Long id, Long status);
    List<CatDepartmentEntity> findByCodeInAndStatus(List<String> codeList, Long status);
    Optional<CatDepartmentEntity> findById(Long id);
}
