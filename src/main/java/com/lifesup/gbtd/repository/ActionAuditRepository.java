package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ActionAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionAuditRepository extends JpaRepository<ActionAuditEntity, Long> {
}
