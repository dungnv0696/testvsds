package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.ServiceGBTDDefineDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;

import java.util.List;

public interface IServiceGBTDDefine {
    List<ServiceGBTDDefineDto> getAllDefineService(Long serviceId, Long deptId);
    void add(List<ServiceGBTDDefineDto> dtos);
    void update(List<ServiceGBTDDefineDto> dtos);
    void delete(ServiceGBTDDefineDto dto);
}
