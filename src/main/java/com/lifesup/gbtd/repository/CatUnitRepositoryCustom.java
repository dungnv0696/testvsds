package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.CatUnitDto;
import com.lifesup.gbtd.dto.object.CatUnitRateDto;
import com.lifesup.gbtd.model.CatUnitEntity;

import java.util.List;

public interface CatUnitRepositoryCustom {
    List<CatUnitRateDto> findConverter(Long before, List<Long> after);

    List<CatUnitEntity> findWithParam(CatUnitDto dto);
}
