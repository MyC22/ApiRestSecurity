package com.example.RestApi.Persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action; // "Created", "Update", "Delete"
    private String entity; // "User", "Role"
    private String details;
    private Long entityId; //ID del objeto afectado
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "performed_by_id")
    private UserEntity performedBy; //usuario que realizo la acción



    public AuditLogEntity(String action, String entity, Long entityId, UserEntity performedBy) {
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
        this.performedBy = performedBy;
        this.timestamp = LocalDateTime.now(); // Se asigna el timestamp automáticamente
    }

    public static AuditLogEntity create(String action, String entity, Long entityId, UserEntity performedBy, String details) {
        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setAction(action);
        auditLog.setEntity(entity);
        auditLog.setEntityId(entityId);
        auditLog.setPerformedBy(performedBy);
        auditLog.setDetails(details);
        auditLog.setTimestamp(LocalDateTime.now());
        return auditLog;
    }


}
