package com.example.RestApi.model.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private Long entityId; //ID del objeto afectado

    @ManyToOne
    @JoinColumn(name = "performed_by_id")
    private UserEntity performedBy; //usuario que realizo la acción

    private LocalDateTime timestamp = LocalDateTime.now();


    public AuditLogEntity(String action, String entity, Long entityId, UserEntity performedBy) {
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
        this.performedBy = performedBy;
        this.timestamp = LocalDateTime.now(); // Se asigna el timestamp automáticamente
    }

    public static AuditLogEntity create(String action, String entity, Long entityId, UserEntity performedBy) {
        return new AuditLogEntity(action, entity, entityId, performedBy);
    }


}
