package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ConfigChartRoleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IConfigChartRoleService {
    List<ConfigChartRoleDto> get(ConfigChartRoleDto dto, Pageable pageable);
    List<ConfigChartRoleDto> getByChartId(Long chartId);
    void updateConfigChartRole(List<ConfigChartRoleDto> dtos);
    void deleteConfigChartRole(Long id);
}
