package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigChartDefaultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConfigChartDefaultRepository extends JpaRepository<ConfigChartDefaultEntity, Long> {
    @Query(value = "SELECT c FROM ConfigChartDefaultEntity c where c.status = 1 and " +
            "c.typeChart = ?1 and (c.timeType is null or c.timeType = ?2) order by c.orderIndex ")
    List<ConfigChartDefaultEntity> findLookupParams(String typeChart, Long timeType);
}
