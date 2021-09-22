package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.CatUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CatUnitRepository extends JpaRepository<CatUnitEntity, Long>, CatUnitRepositoryCustom {
    List<CatUnitEntity> findAllByStatus(Long status);
    Optional<CatUnitEntity> findByIdAndStatus(Long id, Long status);
    List<CatUnitEntity> findByIdInAndStatus(List<Long> idList, Long status);
}
