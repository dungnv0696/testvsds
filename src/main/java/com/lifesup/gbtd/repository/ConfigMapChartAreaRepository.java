package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigMapChartAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigMapChartAreaRepository extends JpaRepository<ConfigMapChartAreaEntity, Long> {
    List<ConfigMapChartAreaEntity> findByAreaId(Long areaId);
    List<ConfigMapChartAreaEntity> findByAreaIdIn(List<Long> areaIdList);
    int deleteByAreaIdIn(List<Long> areaIdList);
    List<ConfigMapChartAreaEntity> findByDashboardIdNextto(Long dashboardIdNextto);
    List<ConfigMapChartAreaEntity> findByChartId(Long chartId);
}
