package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ConfigMenuItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.util.List;

public interface IConfigMenuItemService {
    List<ConfigMenuItemDto> findAll(String keyword, Long[] menuIds, Long isDefault, Long status);
    Page<ConfigMenuItemDto> findAllByMenu(ConfigMenuItemDto dto, Pageable pageable);
    List<ConfigMenuItemDto> updateConfigMenuItem(List<ConfigMenuItemDto> dtos);
    void delete(Long id);
}
