package com.example.RestApi.handler.impl;

import com.example.RestApi.Services.RoleService;
import com.example.RestApi.Services.UserDBService;
import com.example.RestApi.handler.RoleHandler;
import com.example.RestApi.model.dto.RoleDTO;
import com.example.RestApi.model.dto.UserDTO;
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
import java.util.Optional;

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
        Optional<UserEntity> userEntityOpt = userDBService.getUserById(userId);

        if (userEntityOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        UserEntity user = userEntityOpt.get();

        //Delegar lógica a RoleService
        Optional<UserDTO> updatedUserOpt = roleService.addRoleToUser(user, roleNames);

        if (updatedUserOpt.isEmpty()) {
            throw new RuntimeException("Failed to assign roles");
        }

        //ACTUALIZAR LA SESIÓN DEL USUARIO
        updateUserAuthorities(user);

        return updatedUserOpt.get();
    }


    @Override
    public UserDTO removeRoleFromUser(Long userId, List<String> roleNames) {
        Optional<UserEntity> userOptional = userDBService.getUserById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        UserEntity user = userOptional.get();

        Optional<UserDTO> updatedUserOpt = roleService.removeRoleFromUser(user, roleNames);

        if (updatedUserOpt.isEmpty()) {
            throw new RuntimeException("Roles not assigned to the user");
        }

        UserDTO updatedUser = updatedUserOpt.get();

        //ACTUALIZAR LA SESIÓN DEL USUARIO
        updateUserAuthorities(user);

        return updatedUser;
    }


    private void updateUserAuthorities(UserEntity user) {
        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<>();

        //Agregar roles actualizados
        user.getRoles().forEach(role ->
                updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name())));

        //Agregar permisos actualizados
        user.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> updatedAuthorities.add(new SimpleGrantedAuthority(permission.getName())));

        //Obtener la autenticación actual
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
