package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.DashboardReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DashboardReportRepository extends JpaRepository<DashboardReportEntity, Long>, DashboardReportRepositoryCustom {
    List<DashboardReportEntity> findByFolderId(Long folderId);
    List<DashboardReportEntity> findByReportCode(String reportCode);
}