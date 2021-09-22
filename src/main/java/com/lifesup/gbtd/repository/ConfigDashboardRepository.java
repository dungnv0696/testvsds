package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigDashboardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigDashboardRepository extends JpaRepository<ConfigDashboardEntity, Long>, ConfigDashboardRepositoryCustom {
    List<ConfigDashboardEntity> findByProfileId(Long profileId);
    List<ConfigDashboardEntity> findByProfileIdAndDashboardType(Long profileId, Long dashboardType);
    List<ConfigDashboardEntity> findByMenuItemId(Long menuItemId);
}
