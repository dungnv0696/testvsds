package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.ConfigMenuDto;
import com.lifesup.gbtd.dto.object.ConfigMenuItemDto;
import com.lifesup.gbtd.model.ConfigMenuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConfigMenuRepositoryCustom {

    List<ConfigMenuItemDto> getConfigMenuItemsByMenuIdAndProfileId(Long menuId, Long profileId);
    List findAllByProfileIds(Long[] profileIds);
    Page<ConfigMenuDto> getListConfigMenu(ConfigMenuDto dto, Pageable pageable);

    List<ConfigMenuEntity> getAllConfigMenu(ConfigMenuDto dto);
}
