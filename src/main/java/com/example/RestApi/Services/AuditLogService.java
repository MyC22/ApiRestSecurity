package com.example.RestApi.Services;

import com.example.RestApi.Persistence.DTO.AuditLogDto;
import com.example.RestApi.Persistence.Repository.AuditLogMapper;
import com.example.RestApi.Persistence.Repository.AuditLogRepository;
import com.example.RestApi.Persistence.entity.AuditLogEntity;
import com.example.RestApi.Persistence.entity.UserEntity;
import com.example.RestApi.Utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final SecurityUtil securityUtil; // 🔹 Inyectamos SecurityUtil

    public void registerAudit(String action, String entity, Long entityId) {
        try {
            UserEntity performedBy = securityUtil.getAuthenticatedUser(); // 🔹 Obtenemos el usuario autenticado

            AuditLogEntity logEntry = AuditLogEntity.create(action, entity, entityId, performedBy);
            auditLogRepository.save(logEntry);
            log.info("Audit log registered: {} - {} (ID: {}) by {}", action, entity, entityId, performedBy.getUsername());
        } catch (Exception e) {
            log.error("Error registering audit log: {}", e.getMessage(), e);
        }
    }

    public List<AuditLogDto> getAllLogs() {
        return auditLogRepository.findAll().stream()
                .map(auditLogMapper::toDto)
                .toList();
    }
}
