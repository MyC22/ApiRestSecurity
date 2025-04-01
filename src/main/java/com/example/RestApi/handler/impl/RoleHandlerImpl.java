package com.example.RestApi.handler.impl;

import com.example.RestApi.Exceptions.RoleAssignmentException;
import com.example.RestApi.Exceptions.RoleNotFoundException;
import com.example.RestApi.Exceptions.UserNotFoundException;
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
        try {
            UserEntity user = userDBService.getUserById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
            Set<RoleEntity> rolesToAdd = userDBService.findRolesByNames(roleNames);
            if (rolesToAdd.isEmpty()) {
                userDBService.logUserAction("ADD_ROLE", userDBService.convertToDTO(user), userId,
                        "Intento fallido: No se encontró el rol '" + roleNames + "'", false, true);
                throw new RoleNotFoundException("No valid roles found to add: " + roleNames);
            }
            roleService.validateRolesNotAssigned(user.getRoles(), rolesToAdd);
            UserEntity updatedUserEntity = userDBService.addRoleToUser(userId, rolesToAdd)
                    .orElseThrow(() -> new RoleAssignmentException("Failed to add roles to user"));
            roleNames.forEach(roleName -> userDBService.logUserAction(
                    "ADD_ROLE",
                    userDBService.convertToDTO(user),
                    userId,
                    "Se añadió el rol '" + roleName + "'",
                    true
            ));

            updateUserAuthorities(updatedUserEntity);
            return userDBService.convertToDTO(updatedUserEntity);
        } catch (UserNotFoundException | RoleNotFoundException | RoleAssignmentException e) {
            userDBService.logUserAction("ADD_ROLE", null, userId,
                    "Error al añadir roles: " + e.getMessage(), false, true);
            throw e;
        } catch (Exception e) {
            userDBService.logUserAction("ADD_ROLE", null, userId,
                    "Error inesperado al añadir roles: " + e.getMessage(), false, true);
            throw new RoleAssignmentException("Unexpected error while adding roles.");
        }
    }

    @Override
    public UserDTO removeRoleFromUser(Long userId, List<String> roleNames) {
        try {
            UserEntity user = userDBService.getUserById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
            Set<RoleEntity> rolesToRemove = userDBService.findRolesByNames(roleNames);
            if (rolesToRemove.isEmpty()) {
                userDBService.logUserAction("ADD_ROLE", userDBService.convertToDTO(user), userId,
                        "Intento fallido: No se encontró el rol '" + roleNames + "'", false, true);
                throw new RoleNotFoundException("No valid roles found to remove: " + roleNames);
            }
            roleService.validateRolesAssigned(user.getRoles(), rolesToRemove);
            UserEntity updatedUserEntity = userDBService.removeRoleFromUser(userId, rolesToRemove)
                    .orElseThrow(() -> new RuntimeException("Failed to remove roles from user"));
            roleNames.forEach(roleName -> userDBService.logUserAction(
                    "REMOVE_ROLE",
                    userDBService.convertToDTO(user),
                    userId,
                    "Se eliminó el rol '" + roleName + "'",
                    true
            ));
            updateUserAuthorities(updatedUserEntity);
            return userDBService.convertToDTO(updatedUserEntity);
        }catch (UserNotFoundException | RoleNotFoundException | RoleAssignmentException e){
            userDBService.logUserAction("ADD_ROLE", null, userId,
                    "Error al añadir roles: " + e.getMessage(), false, true);
            throw e;
        } catch (Exception e) {
            userDBService.logUserAction("ADD_ROLE", null, userId,
                    "Error inesperado al añadir roles: " + e.getMessage(), false, true);
            throw new RoleAssignmentException("Unexpected error while adding roles.");
        }
    }

    private void updateUserAuthorities(UserEntity user) {
        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<>();
        user.getRoles().forEach(role ->
                updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name())));
        user.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> updatedAuthorities.add(new SimpleGrantedAuthority(permission.getName())));
        // Obtener la autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails updatedUserDetails = new User(user.getUsername(), user.getPassword(), updatedAuthorities);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails, authentication.getCredentials(), updatedAuthorities);

            // Reemplazar la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}