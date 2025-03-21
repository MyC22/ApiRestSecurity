package com.example.RestApi.Services;

import com.example.RestApi.Exceptions.RoleAlreadyAssignedException;
import com.example.RestApi.Exceptions.RoleNotAssignedException;
import com.example.RestApi.model.dto.RoleDTO;
import com.example.RestApi.model.dto.UserDTO;
import com.example.RestApi.Mappers.PermissionMapper;
import com.example.RestApi.Mappers.RoleMapper;
import com.example.RestApi.Mappers.UserMapper;
import com.example.RestApi.model.entity.RoleEntity;
import com.example.RestApi.Enums.RoleEnum;
import com.example.RestApi.model.entity.UserEntity;
import com.example.RestApi.Repository.RoleRepository;
import com.example.RestApi.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserDetailServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private AuditLogService auditLogService;


    public Optional<UserDTO> addRoleToUser(UserEntity user, List<String> roleNames) {
        // Convertir nombres de roles a RoleEnum
        List<RoleEnum> roleEnums = convertToRoleEnums(roleNames);

        // Buscar los roles en la base de datos
        Set<RoleEntity> newRoles = new HashSet<>(roleRepository.findRoleEntitiesByRoleNameIn(roleEnums));

        if (newRoles.isEmpty()) {
            throw new IllegalArgumentException("The specified roles do not exist");
        }

        // Verificar si ya tiene los roles
        if (user.getRoles().containsAll(newRoles)) {
            throw new RoleAlreadyAssignedException("The user already has these roles assigned.");
        }

        // Agregar los nuevos roles sin reemplazar los existentes
        user.getRoles().addAll(newRoles);

        // Guardar cambios
        UserEntity updatedUser = userRepository.save(user);

        // Registrar auditoría para cada rol agregado
        newRoles.forEach(role -> auditLogService.logUserAction("ADD_ROLE", user.getId(), user.getUsername(),
                "Asignó el rol '" + role.getRoleName() + "'"));

        return Optional.of(userMapper.toDTO(updatedUser));
    }


    public Optional<UserDTO> removeRoleFromUser(UserEntity user, List<String> roleNames) {
        // Convertir nombres de roles a RoleEnum
        List<RoleEnum> roleEnums = convertToRoleEnums(roleNames);

        // Buscar los roles en la base de datos
        Set<RoleEntity> rolesToRemove = new HashSet<>(roleRepository.findRoleEntitiesByRoleNameIn(roleEnums));

        if (rolesToRemove.isEmpty()) {
            throw new IllegalArgumentException("The specified roles do not exist");
        }

        // Verificar que el usuario tenga estos roles antes de eliminarlos
        if (!user.getRoles().containsAll(rolesToRemove)) {
            throw new RoleNotAssignedException("The user does not have all the specified roles.");
        }

        // Eliminar los roles
        user.getRoles().removeAll(rolesToRemove);

        // Guardar cambios en la base de datos
        UserEntity updatedUser = userRepository.save(user);

        // Registrar auditoría
        rolesToRemove.forEach(role -> auditLogService.logUserAction("REMOVE_ROLE", user.getId(), user.getUsername(),
                "Removió el rol '" + role.getRoleName() + "'"));

        return Optional.of(userMapper.toDTO(updatedUser));
    }


    /**
     * Convierte una lista de nombres de roles en una lista de RoleEnum.
     */
    private List<RoleEnum> convertToRoleEnums(List<String> roleNames) {
        return roleNames.stream()
                .map(RoleEnum::valueOf)
                .collect(Collectors.toList());
    }











}
