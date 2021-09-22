package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.CatUnitDto;
import com.lifesup.gbtd.dto.object.CatUnitRateDto;

import java.util.List;

public interface ICatUnitService {
    List<CatUnitDto> findByStatus(Long status);
    CatUnitDto findById(Long id);
    List<CatUnitDto> findByIds(List<Long> ids);
    List<CatUnitRateDto> findConverter(Long before, List<Long> after);
    List<CatUnitRateDto> findConverter(Long before, Long after);

    List<CatUnitDto> getAll(CatUnitDto dto);
}
