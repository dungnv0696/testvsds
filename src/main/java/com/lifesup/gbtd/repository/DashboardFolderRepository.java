package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.DashboardFolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DashboardFolderRepository extends JpaRepository<DashboardFolderEntity, Long>, DashboardFolderRepositoryCustom {
    @Query(value = "SELECT DISTINCT d.folderParentId FROM DashboardFolderEntity d")
    List<Long> getFolderParentId();
    List<DashboardFolderEntity> findByFolderIdInAndStatus(List<Long> folderIds, Long status);
    List<DashboardFolderEntity> findByFolderCode(String folderCode);
}