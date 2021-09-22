package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigMenuRepository extends JpaRepository<ConfigMenuEntity, Long>, ConfigMenuRepositoryCustom {
    List<ConfigMenuEntity> findByStatusOrderByOrderIndexAscMenuNameAsc(Long status);
    List<ConfigMenuEntity> findByProfileId(Long profileId);
    List<ConfigMenuEntity> findByMenuNameAndProfileId(String menuName, Long profileId);
}
