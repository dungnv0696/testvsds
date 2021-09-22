package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigProfileRoleEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigProfileRoleRepository extends JpaRepository<ConfigProfileRoleEntity, Long> {
    ConfigProfileRoleEntity findByProfileIdAndDeptId(Long profileId, Long deptId);
    ConfigProfileRoleEntity findByProfileIdAndUsernameUsed(Long profileId, String usernameUsed);
    List<ConfigProfileRoleEntity> findByProfileIdAndRoleCodeAndIdIsNot(Long profileId, String roleCode, Long id);
    List<ConfigProfileRoleEntity> findByProfileId(Long profileId, Pageable pageable);
    List<ConfigProfileRoleEntity> findByProfileId(Long profileId);
    Long countByProfileId(Long profileId);
}
