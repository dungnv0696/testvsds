package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    List<CategoryEntity> findAllByOrderByCategoryNameAsc();
}
