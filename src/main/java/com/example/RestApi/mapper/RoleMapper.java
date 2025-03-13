package com.example.RestApi.mapper;

import com.example.RestApi.model.dto.AuditLogDto;
import com.example.RestApi.model.dto.NotUserDto;
import com.example.RestApi.model.dto.UserDto;
import com.example.RestApi.model.entity.AuditLogEntity;
import com.example.RestApi.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    UserDto mapUserEntityToUserDto(UserEntity entity);

    AuditLogEntity mapAuditLogDtoToEntity(AuditLogDto auditLogDto);
}
