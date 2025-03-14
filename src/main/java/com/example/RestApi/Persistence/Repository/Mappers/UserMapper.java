package com.example.RestApi.Persistence.Repository.Mappers;

import com.example.RestApi.Persistence.DTO.UserDTO;
import com.example.RestApi.Persistence.entity.RoleEntity;
import com.example.RestApi.Persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToEnums")
    @Mapping(target = "permissions", source = "roles", qualifiedByName = "mapPermissionsToStrings")
    @Mapping(source = "enabled", target = "isEnabled")
    UserDTO toDTO(UserEntity userEntity);

    @Named("mapRolesToEnums")
    default Set<String> mapRolesToEnums(Set<RoleEntity> roles) {
        return roles.stream()
                .map(role -> role.getRoleName().name()) // 🔹 Convertimos RoleEnum a String
                .collect(Collectors.toSet());
    }

    @Named("mapPermissionsToStrings")
    default Set<String> mapPermissionsToStrings(Set<RoleEntity> roles) {
        return roles.stream()
                .flatMap(role -> role.getPermissionList().stream())
                .map(permission -> permission.getName())
                .collect(Collectors.toSet());
    }
}
