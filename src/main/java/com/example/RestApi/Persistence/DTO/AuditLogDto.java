package com.example.RestApi.Persistence.DTO;

import java.time.LocalDateTime;

public record AuditLogDto(
        Long id,
        String action,
        String entity,
        Long entityId,
        String performedBy,
        LocalDateTime timestamp // Ahora usamos LocalDateTime directamente
) { }
