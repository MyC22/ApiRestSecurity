package com.example.RestApi.handler.impl;

import com.example.RestApi.Services.AuditLogService;
import com.example.RestApi.Services.RoleService;
import com.example.RestApi.Services.UserDBService;
import com.example.RestApi.handler.RoleHandler;
import com.example.RestApi.model.dto.RoleDTO;
import com.example.RestApi.model.dto.UserDTO;
import com.example.RestApi.model.entity.RoleEntity;
import com.example.RestApi.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component(value = "roleHandlerImpl")
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class RoleHandlerImpl implements RoleHandler {

    private UserDBService userDBService;
    private RoleService roleService;
    private AuditLogService auditLogService;

    @Override
    public List<RoleDTO> getAllRoles() {
        return userDBService.getAllRoles();
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        return userDBService.getRoleById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));
    }

    @Override
    public UserDTO addRoleToUser(Long userId, List<String> roleNames) {
        UserEntity user = userDBService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Set<RoleEntity> rolesToAdd = userDBService.findRolesByNames(roleNames);
        if (rolesToAdd.isEmpty()) {
            throw new RuntimeException("No valid roles found to add: " + roleNames);
        }

        roleService.validateRolesNotAssigned(user.getRoles(), rolesToAdd);

        UserEntity updatedUserEntity = userDBService.addRoleToUser(userId, rolesToAdd)
                .orElseThrow(() -> new RuntimeException("Failed to add roles to user"));

        roleNames.forEach(roleName -> auditLogService.logUserAction(
                "ADD_ROLE",
                userId,
                user.getUsername(),
                "Se añadió el rol '" + roleName + "'"
        ));

        updateUserAuthorities(updatedUserEntity);
        return userDBService.convertToDTO(updatedUserEntity);
    }

    @Override
    public UserDTO removeRoleFromUser(Long userId, List<String> roleNames) {
        UserEntity user = userDBService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Set<RoleEntity> rolesToRemove = userDBService.findRolesByNames(roleNames);
        if (rolesToRemove.isEmpty()) {
            throw new RuntimeException("No valid roles found to remove: " + roleNames);
        }

        // Validar si los roles están asignados antes de eliminarlos
        roleService.validateRolesAssigned(user.getRoles(), rolesToRemove);

        UserEntity updatedUserEntity = userDBService.removeRoleFromUser(userId, rolesToRemove)
                .orElseThrow(() -> new RuntimeException("Failed to remove roles from user"));

        roleNames.forEach(roleName -> auditLogService.logUserAction(
                "REMOVE_ROLE",
                userId,
                user.getUsername(),
                "Se eliminó el rol '" + roleName + "'"
        ));

        updateUserAuthorities(updatedUserEntity);
        return userDBService.convertToDTO(updatedUserEntity);
    }



    private void updateUserAuthorities(UserEntity user) {
        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<>();

        // Agregar roles actualizados
        user.getRoles().forEach(role ->
                updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name())));

        // Agregar permisos actualizados
        user.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> updatedAuthorities.add(new SimpleGrantedAuthority(permission.getName())));

        // Obtener la autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Crear nueva autenticación con los roles actualizados
            UserDetails updatedUserDetails = new User(user.getUsername(), user.getPassword(), updatedAuthorities);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails, authentication.getCredentials(), updatedAuthorities);

            // Reemplazar la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}