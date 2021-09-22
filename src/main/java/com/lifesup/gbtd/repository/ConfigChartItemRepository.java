package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigChartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigChartItemRepository extends JpaRepository<ConfigChartItemEntity, Long> {
    List<ConfigChartItemEntity> findByChartIdAndStatus(Long chartId, Long status);
    List<ConfigChartItemEntity> findByChartId(Long chartId);
    int deleteByIdIn(List<Long> idList);
}
