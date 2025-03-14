package com.example.RestApi.Persistence.Repository.Mappers;

import com.example.RestApi.Persistence.DTO.AuditLogDto;
import com.example.RestApi.Persistence.entity.AuditLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "performedBy", source = "performedBy.username") // Extrae solo el username
    AuditLogDto toDto(AuditLogEntity entity);
}
