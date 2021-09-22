package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.ServicesMapDeptDto;

import java.util.List;

public interface ServicesMapDeptRepositoryCustom {
    List<ServicesMapDeptDto> findWithServiceIds(List<Long> serviceIds, String username);
}
