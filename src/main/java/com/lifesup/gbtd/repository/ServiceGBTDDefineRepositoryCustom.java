package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.ServiceGBTDDefineDto;

import java.util.List;

public interface ServiceGBTDDefineRepositoryCustom {
    List<ServiceGBTDDefineDto> findByServiceIdAndDeptIds(Long serviceId, Long deptId);
}
