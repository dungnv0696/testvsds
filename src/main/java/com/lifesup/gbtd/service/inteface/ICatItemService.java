package com.lifesup.gbtd.service.inteface;

import com.lifesup.gbtd.dto.object.CatItemDto;

import java.util.List;
import java.util.Optional;

public interface ICatItemService {
    List<CatItemDto> getCatItems();

    void add(CatItemDto dto);

    void update(CatItemDto dto);

    List<CatItemDto> findCatItems(CatItemDto dto);

    List<CatItemDto> findByIds(List<Long> itemIds);

    void deleteCatItemById(Long id);

    List<CatItemDto> findByCategoryCode(String categoryCode);

    List<CatItemDto> findByCategoryCodeAndStatus(String categoryCode, Long status);

    List<String> findServiceSourceByDeptCode(String deptCode);

    Optional<CatItemDto> findByCode(String code);

    List<CatItemDto> findAll(CatItemDto catItemDto);
}
