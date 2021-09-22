package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ConfigProfileRoleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IConfigProfileRoleService {
    void updateConfigProfileRole(List<ConfigProfileRoleDto> dtos);
    void deleteConfigProfileRole(Long id);
    Page<ConfigProfileRoleDto> findProfileRoles(Long profileId, Pageable pageable);
}
