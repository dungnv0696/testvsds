package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.BiTdServicesTreeDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDChartDto;
import com.lifesup.gbtd.dto.object.ServiceGBTDDto;
import com.lifesup.gbtd.dto.object.ServicesMapDeptDto;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface IServiceGBTDService {
    List<ServiceGBTDDto> findServiceGBTDs(ServiceGBTDDto dto);

    List<ServicesMapDeptDto> findWithServiceIds(List<Long> serviceIds, String username);

    void add(ServiceGBTDDto serviceGBTDDto);

    void update(ServiceGBTDDto serviceGBTDDto);

    void delete(Long serviceId);

    ServiceGBTDDto findById(Long id);

    List<ServiceGBTDDto> findByServiceId(Long serviceId);

    List<BiTdServicesTreeDto> getListServiceFormula(ServiceGBTDDto dto);

    List<ServiceGBTDDto> getAllServiceGbtd();

    @Transactional
    void saveServiceFormula(List<BiTdServicesTreeDto> dtos);

    @Transactional
    void updateServiceFormula(List<BiTdServicesTreeDto> dtos);

    @Transactional
    void deleteServiceFormula(BiTdServicesTreeDto dto);

    List<ServiceGBTDDto> findServicesByDeptId(Long deptId, Long parentServiceId);

    List<ServiceGBTDDto> findByIds(List<Long> serviceIds);

    Optional<ServiceGBTDChartDto> findByKpiIdWithRate(Long kpiId, Long unitIdView);

    String haveFormulaOrDefine(ServiceGBTDDto dto);

    List<ServiceGBTDDto> findServiceOfDept(ServiceGBTDDto dto);

    List<ServiceGBTDDto> findChildrenService(ServiceGBTDDto dto);
}
