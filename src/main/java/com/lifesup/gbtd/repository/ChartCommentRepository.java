package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ChartCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChartCommentRepository extends JpaRepository<ChartCommentEntity, Long>, ChartCommentRepositoryCustom {
    Optional<ChartCommentEntity> findByIdAndUserName(Long id, String userName);
}
