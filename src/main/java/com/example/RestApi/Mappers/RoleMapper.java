package com.example.RestApi.Mappers;

import com.example.RestApi.model.dto.RoleDTO;
import com.example.RestApi.model.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    //RoleEntity a RoleDTO
    @Mapping(target = "roleName", source = "roleName")
    @Mapping(target = "permissionList", source = "permissionList")
    RoleDTO toDTO(RoleEntity roleEntity);

    //RoleDTO a RoleEntity
    @Mapping(target = "roleName", source = "roleName")
    @Mapping(target = "permissionList", source = "permissionList")
    RoleEntity toEntity(RoleDTO roleDTO);
}
