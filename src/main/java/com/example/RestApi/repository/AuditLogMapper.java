package com.example.RestApi.repository;

import com.example.RestApi.model.dto.AuditLogRecord;
import com.example.RestApi.model.entity.AuditLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "performedBy", source = "performedBy.username") // Extrae solo el username
    AuditLogRecord toDto(AuditLogEntity entity);
}
