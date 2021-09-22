package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigChartRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigChartRoleRepository extends JpaRepository<ConfigChartRoleEntity, Long>, ConfigChartRoleRepositoryCustom {
    List<ConfigChartRoleEntity> findByChartId(Long chartId);
    ConfigChartRoleEntity findByChartIdAndDeptId(Long chartId, Long deptId);
    ConfigChartRoleEntity findByChartIdAndUsernameUsed(Long chartId, String userNameUsed);
    List<ConfigChartRoleEntity> findByChartIdAndRoleCodeAndIdIsNot(Long chartId, String roleCode, Long id);
}
