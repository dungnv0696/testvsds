package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.CatLocationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatLocationRepository extends JpaRepository<CatLocationEntity, Long> {
    List<CatLocationEntity> findByCountryIdNotAndLocationLevel(Long countryId, Long LocationLevel, Pageable pageableCustom);
    List<CatLocationEntity> findByCountryIdAndLocationLevel(Long countryId, Long LocationLevel, Pageable pageableCustom);
    List<CatLocationEntity> findByProvinceIdAndLocationLevel(Long provinceId, Long LocationLevel, Pageable pageableCustom);
}