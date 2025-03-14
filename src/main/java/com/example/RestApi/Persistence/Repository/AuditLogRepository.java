package com.example.RestApi.Persistence.Repository;

import com.example.RestApi.Persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findTop10ByOrderByTimestampDesc();
}
