package com.example.RestApi.model.dto;

import java.time.LocalDateTime;

public record AuditLogRecord(
        Long id,
        String action,
        String entity,
        Long entityId,
        String details,
        String performedBy,
        LocalDateTime timestamp
) {
}
