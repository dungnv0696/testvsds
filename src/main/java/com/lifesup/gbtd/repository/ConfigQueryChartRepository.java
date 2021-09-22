package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigQueryChartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConfigQueryChartRepository extends JpaRepository<ConfigQueryChartEntity, Long>, ConfigQueryChartRepositoryCustom {
    List<ConfigQueryChartEntity> findByIdIn(List<Long> idList);
}
