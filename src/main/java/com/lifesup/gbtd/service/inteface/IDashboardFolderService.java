package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.DashboardFolderDto;
import com.lifesup.gbtd.dto.object.TreeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDashboardFolderService {
    List<DashboardFolderDto> getDashboardFolderByFolderId(List<Long> folderIds);
    List<DashboardFolderDto> getDashboardFolderTree(DashboardFolderDto dto);
    Page<DashboardFolderDto> doSearch(DashboardFolderDto dto, Pageable pageable);
    DashboardFolderDto findById(Long id);
    DashboardFolderDto create(DashboardFolderDto dto);
    DashboardFolderDto update(DashboardFolderDto dto);
    DashboardFolderDto changeFolderStatus(DashboardFolderDto dto);
    List<TreeDto> getFolderAndFile();
}