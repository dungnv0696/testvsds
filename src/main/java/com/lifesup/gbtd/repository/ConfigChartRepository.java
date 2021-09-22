package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigChartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigChartRepository extends JpaRepository<ConfigChartEntity, Long>, ConfigChartRepositoryCustom {

}
