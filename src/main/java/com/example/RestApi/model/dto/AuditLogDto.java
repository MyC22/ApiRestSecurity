package com.example.RestApi.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuditLogDto(
        Long id,
        String action,
        String entity,
        Long entityId,
        String details,
        String performedBy,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
}
