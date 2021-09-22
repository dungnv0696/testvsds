package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.ConfigChartRoleDto;
import com.lifesup.gbtd.model.ConfigChartRoleEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConfigChartRoleRepositoryCustom {

    List<ConfigChartRoleEntity> findAll(ConfigChartRoleDto dto, Pageable pageable);
}
