package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.NotShowIntroductionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotShowIntroductionRepository extends JpaRepository<NotShowIntroductionEntity,Long> {
    List<NotShowIntroductionEntity> findByUserId(Long userid);
}
