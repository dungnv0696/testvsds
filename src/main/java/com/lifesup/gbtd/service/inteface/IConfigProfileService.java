package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ConfigProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IConfigProfileService {

    ConfigProfileDto save(ConfigProfileDto configProfileDto);
    ConfigProfileDto update(ConfigProfileDto configProfileDto);
    void delete(Long id);
    Page<ConfigProfileDto> getAllConfigProfiles(ConfigProfileDto configProfileDto, Pageable pageable);
    ConfigProfileDto findById(Long id);
    ConfigProfileDto copy(Long cloneId);
    List<ConfigProfileDto> getAllConfigProfilesAndOrderBy(ConfigProfileDto configProfileDto, Pageable pageable);
}
