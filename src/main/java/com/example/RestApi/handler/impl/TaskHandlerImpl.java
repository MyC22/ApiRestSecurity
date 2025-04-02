package com.example.RestApi.handler.impl;

import com.example.RestApi.Services.TaskService;
import com.example.RestApi.Services.UserDBService;
import com.example.RestApi.handler.TaskHandler;
import com.example.RestApi.model.dto.TaskDTO;
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

@Component(value = "taskHandlerImpl")
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class TaskHandlerImpl implements TaskHandler {

    private UserDBService userDBService;
    private TaskService taskService;

    @Override
    public TaskDTO assignTaskToUser(Long userId, TaskDTO taskDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("Acceso no autorizado. No se encontró el usuario autenticado.");
        }

        taskService.validateTaskBeforeAssignment(taskDTO);

        UserEntity user = userDBService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + userId));

        TaskDTO assignedTask = userDBService.assignTaskToUser(userId, taskDTO);

        updateUserAuthorities(user);

        return assignedTask;
    }

    private void updateUserAuthorities(UserEntity user) {
        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<>();

        user.getRoles().forEach(role ->
                updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name())));
        user.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> updatedAuthorities.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails updatedUserDetails = new User(user.getUsername(), user.getPassword(), updatedAuthorities);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUserDetails, authentication.getCredentials(), updatedAuthorities);

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}
