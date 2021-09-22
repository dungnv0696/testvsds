package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ConfigMenuDto;
import com.lifesup.gbtd.dto.object.ConfigMenuItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IConfigMenuService {
    List<ConfigMenuDto> getAllConfigMenus(ConfigMenuDto dto);
    List<ConfigMenuItemDto> getConfigMenuItemsByMenuIdAndProfileId(Long menuId, Long profileId);
    ConfigMenuDto findOne(Long id);
    List<ConfigMenuDto> findAllByProfileIds(Long[] profileIds);
    Page<ConfigMenuDto> doSearch(ConfigMenuDto dto, Pageable pageable);
    ConfigMenuDto create(ConfigMenuDto dto);
    ConfigMenuDto update(ConfigMenuDto dto);
    void checkDelete(Long id);
    void delete(Long id);
}
