package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.SsoMapDeptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SsoMapDeptRepository extends JpaRepository<SsoMapDeptEntity, Long> {

    List<SsoMapDeptEntity> findBySsoDeptId(Long ssoDeptId);
}
