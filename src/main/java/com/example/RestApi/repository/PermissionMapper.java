package com.example.RestApi.repository;

import com.example.RestApi.model.dto.PermissionDto;
import com.example.RestApi.model.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    // Convertir de Entity a DTO
    PermissionDto toDTO(PermissionEntity permissionEntity);

    // Convertir de DTO a Entity
    PermissionEntity toEntity(PermissionDto permissionDTO);

    // Para listas de permisos
    default Set<PermissionDto> toDTOSet(Set<PermissionEntity> permissionEntities) {
        return permissionEntities.stream()
                .map(this::toDTO)
                .collect(Collectors.toSet());
    }

    default Set<PermissionEntity> toEntitySet(Set<PermissionDto> permissionDtos) {
        return permissionDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }
}
