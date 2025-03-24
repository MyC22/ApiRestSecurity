package com.example.RestApi.Services;

import com.example.RestApi.model.dto.AuditLogDto;
import com.example.RestApi.Mappers.AuditLogMapper;
import com.example.RestApi.Repository.AuditLogRepository;
import com.example.RestApi.model.entity.AuditLogEntity;
import com.example.RestApi.model.entity.UserEntity;
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
    private final UserDetailServiceImpl userDetailService;

    public void registerAudit(String action, String entity, Long entityId, String details) {
        try {
            UserEntity performedBy = userDetailService.getAuthenticatedUser(); //Obtenemos el usuario autenticado

            AuditLogEntity logEntry = AuditLogEntity.create(action, entity, entityId, performedBy, details);
            auditLogRepository.save(logEntry);
            log.info("Audit log registered: {} - {} (ID: {}) by {} - Details: {}",
                    action, entity, entityId, performedBy.getUsername(), details);
        } catch (Exception e) {
            log.error("Error registering audit log: {}", e.getMessage(), e);
        }
    }

    public void logUserAction(String action, Long userId, String username, String details) {
        String fullDetails = String.format("Usuario: %s (ID: %d) - %s", username, userId, details);
        registerAudit(action, "User", userId, fullDetails);
    }

    public List<AuditLogDto> getAllLogs() {
        return auditLogRepository.findAll().stream()
                .map(auditLogMapper::toDto)
                .toList();
    }


}
