package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.CatGroupChartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatGroupChartRepository extends JpaRepository<CatGroupChartEntity, Long>, CatGroupChartRepositoryCustom {
    List<CatGroupChartEntity> findByGroupKpiCodeAndStatus(String code, Long status);
}
