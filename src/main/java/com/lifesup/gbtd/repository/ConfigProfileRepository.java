package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the ConfigProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConfigProfileRepository extends JpaRepository<ConfigProfileEntity, Long>, ConfigProfileRepositoryCustom {
    ConfigProfileEntity findFirstByOrderByIdDesc();
}
