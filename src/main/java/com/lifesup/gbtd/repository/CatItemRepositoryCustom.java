package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.dto.object.CatItemDto;
import com.lifesup.gbtd.model.CatItemEntity;

import java.util.List;


public interface CatItemRepositoryCustom {
    List<CatItemDto> getCatItems();

    List<CatItemDto> findCatItems(CatItemDto dto);

    int deleteCatItemById(Long id);

    boolean validateKeysAddAndUpdate(String categoryCode, String itemCode, Long parentItemId);

    List<String> findByCatCodeAndItemCode(String categoryCode, String itemCode);

    CatItemDto findByCatCodeAndItemCodeAndParentItemId(String categoryCode, String itemCode, Long parentItemId);

    List<CatItemEntity> findAll(CatItemDto dto);
}
