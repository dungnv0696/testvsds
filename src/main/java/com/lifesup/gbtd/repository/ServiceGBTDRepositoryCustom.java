package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.BiTdServicesTreeDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
import com.lifesup.gbtd.dto.request.ServiceTargetResDto;

import java.util.List;

public interface ServiceGBTDRepositoryCustom {
    List<ServiceGBTDDto> findServiceGBTDs(ServiceGBTDDto dto, String username);

    List<BiTdServicesTreeDto> getListServiceFormula(ServiceGBTDDto dto);

    List<ServiceGBTDDto> getAllServiceGbtd();

    int updateFormulaServiceParent(Long idParent, Long parentDeptId, String formula);

    void delete(Long serviceId);

    List<ServiceGBTDDto> findServiceGBTDByDeptId(ServiceGBTDDto dto);

    List<ServiceGBTDDto> findChildrenService(ServiceGBTDDto dto);

    List<ServiceGBTDDto> findServiceOfDept(ServiceGBTDDto dto);

    List<ServiceGBTDDto> findServiceGBTDByDeptIdIn(ServiceGBTDDto dto);
}
