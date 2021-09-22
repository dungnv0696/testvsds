package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ConfigMenuItemEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigMenuItemRepository extends JpaRepository<ConfigMenuItemEntity, Long>, ConfigMenuItemRepositoryCustom {
    List<ConfigMenuItemEntity> findByMenuId(Long menuId);
    List<ConfigMenuItemEntity> findByChartId(Long chartId);
    List<ConfigMenuItemEntity> findByMenuIdOrderByOrderIndex(Long menuId, Pageable pageable);
    Long countByMenuId(Long menuId);
    List<ConfigMenuItemEntity> findByMenuItemName(String menuItemName);
    List<ConfigMenuItemEntity> findByMenuItemNameAndMenuId(String menuItemName, Long menuId);
    List<ConfigMenuItemEntity> findByMenuIdAndOrderIndexGreaterThan(Long menuId, Long orderIndex);
}