package com.example.RestApi.Mappers;

import com.example.RestApi.model.dto.AuditLogDto;
import com.example.RestApi.model.entity.AuditLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "performedBy", source = "performedBy")
    AuditLogDto toDto(AuditLogEntity entity);

    AuditLogEntity mapAuditLogDtoToEntity(AuditLogDto dto);

}
