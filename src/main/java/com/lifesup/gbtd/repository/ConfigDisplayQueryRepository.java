package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigDisplayQueryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigDisplayQueryRepository extends JpaRepository<ConfigDisplayQueryEntity, Long> {
    List<ConfigDisplayQueryEntity> findByItemChartIdInAndStatus(List<Long> itemChartIdList, Long status);
    List<ConfigDisplayQueryEntity> findByItemChartId(Long itemChartId);
    List<ConfigDisplayQueryEntity> findByItemChartIdIn(List<Long> itemChartIdList);
}
