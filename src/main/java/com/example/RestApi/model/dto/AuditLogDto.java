package com.example.RestApi.model.dto;

import com.example.RestApi.model.entity.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogDto {

    private Long id;
    private String action;
    private String entity;
    private Long entityId;
    private UserEntity performedBy;
    private LocalDateTime timestamp;
}
