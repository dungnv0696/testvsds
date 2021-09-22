package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigAreaEntity;
import com.lifesup.gbtd.model.ConfigDashboardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigAreaRepository extends JpaRepository<ConfigAreaEntity, Long>{
    List<ConfigAreaEntity> findByDashboardId(Long dashboardId);
    int deleteByIdIn(List<Long> idList);
}
