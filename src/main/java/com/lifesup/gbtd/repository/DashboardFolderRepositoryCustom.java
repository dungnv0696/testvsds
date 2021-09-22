package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.DashboardFolderDto;
import com.lifesup.gbtd.dto.object.TreeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DashboardFolderRepositoryCustom {
    List<TreeDto> getDashboardFolderTree(DashboardFolderDto dto);
    List<DashboardFolderDto> getDashboardFolderTrees(DashboardFolderDto dto);
    Page<DashboardFolderDto> doSearch(DashboardFolderDto dto, Pageable pageable);
}