package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.ConfigProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConfigProfileRepositoryCustom {
    Page<ConfigProfileDto> searchConfigProfiles(ConfigProfileDto dto, Pageable pageable);
    Page<ConfigProfileDto> getAllConfigProfiles(Pageable pageable, String username, Long deptId);
    List<ConfigProfileDto> getByDeptIdAndUsernameUsedAndIds(Long deptId, String userNameUsed, List<Long> ids);
    List<ConfigProfileDto> getAllConfigProfilesAndOrderBy(ConfigProfileDto dto, String username, Long deptId, Pageable pageable);
}
