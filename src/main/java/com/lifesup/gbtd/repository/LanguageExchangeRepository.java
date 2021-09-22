package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.LanguageExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageExchangeRepository extends JpaRepository<LanguageExchangeEntity, Long>, LanguageExchangeRepositoryCustom {
    List<LanguageExchangeEntity> findByAppliedBusinessAndBusinessIdAndLeeLocale(String appliedBusiness, Long businessId, String leeLocale);
}
