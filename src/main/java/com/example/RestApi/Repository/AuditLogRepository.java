package com.example.RestApi.Repository;

import com.example.RestApi.model.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findTop10ByOrderByTimestampDesc();

    List<AuditLogEntity> findByIsCriticalTrue();


}
