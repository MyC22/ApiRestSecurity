package com.example.RestApi.Persistence.Repository;

import com.example.RestApi.Persistence.DTO.PermissionDTO;
import com.example.RestApi.Persistence.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    // Convertir de Entity a DTO
    PermissionDTO toDTO(PermissionEntity permissionEntity);

    // Convertir de DTO a Entity
    PermissionEntity toEntity(PermissionDTO permissionDTO);

    // Para listas de permisos
    default Set<PermissionDTO> toDTOSet(Set<PermissionEntity> permissionEntities) {
        return permissionEntities.stream()
                .map(this::toDTO)
                .collect(Collectors.toSet());
    }

    default Set<PermissionEntity> toEntitySet(Set<PermissionDTO> permissionDTOs) {
        return permissionDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }
}
