package com.example.RestApi.repository;

import com.example.RestApi.model.dto.RoleDto;
import com.example.RestApi.model.dto.NotUserDto;
import com.example.RestApi.model.entity.RoleEntity;
import com.example.RestApi.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    // Mapea RoleEntity a RoleDTO
    @Mapping(target = "roleName", source = "roleName") // 🔹 Actualizado
    @Mapping(target = "permissionList", source = "permissionList")
    RoleDto toDTO(RoleEntity roleEntity);

    // Mapea RoleDTO a RoleEntity
    @Mapping(target = "roleName", source = "roleName") // 🔹 Actualizado
    @Mapping(target = "permissionList", source = "permissionList")
    RoleEntity toEntity(RoleDto roleDTO);

    NotUserDto mapUserEntityToUserDto(Optional<UserEntity> entity);
}
