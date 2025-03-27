package com.example.RestApi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action; // CREATE, LOGIN, DELETE_ROLE, etc.
    private String entity;
    private Long entityId;
    private String details;
    private String performedBy;
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private boolean isCritical = false;

    public static AuditLogEntity create(String action, String entity, Long entityId, UserEntity user, String details, String status, boolean isCritical) {
        return AuditLogEntity.builder()
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .details(details)
                .performedBy(user.getUsername())
                .timestamp(LocalDateTime.now())
                .status(status)
                .isCritical(isCritical)
                .build();
    }
}
