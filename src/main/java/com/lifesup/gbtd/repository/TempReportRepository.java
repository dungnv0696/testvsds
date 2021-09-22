package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.TempReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TempReportRepository extends JpaRepository<TempReportEntity, Long> {
    List<TempReportEntity> findByReportNameIgnoreCaseAndOwnerBy(String reportName, String ownerBy);
}
