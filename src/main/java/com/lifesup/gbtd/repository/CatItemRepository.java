package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.CatItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CatItemRepository extends JpaRepository<CatItemEntity, Long>, CatItemRepositoryCustom {
    List<CatItemEntity> findAllByCategoryCode(String categoryCode);

    List<CatItemEntity> findAllByCategoryCodeAndStatus(String categoryCode, Long status);

    List<CatItemEntity> findByItemIdIn(List<Long> itemIds);

    @Query(value = "select ce.itemName from CatItemEntity ce where ce.categoryCode = ?1 order by ce.itemCode")
    List<String> findItemNameByCategoryCode(String categoryCode);

    CatItemEntity findFirstByItemCodeAndStatus(String itemCode, Long status);

    List<CatItemEntity> findByItemCodeAndCategoryCode(String itemCode, String categoryCode);

    List<CatItemEntity> findByParentItemId(Long parentItemId);
}
