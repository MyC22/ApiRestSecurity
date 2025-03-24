package com.example.RestApi.handler.impl;

import com.example.RestApi.Enums.RoleEnum;
import com.example.RestApi.Utils.JWTUtil;
import com.example.RestApi.model.common.LoginUserCommonDto;
import com.example.RestApi.model.dto.AuditLogDto;
import com.example.RestApi.model.dto.UserDTO;
import com.example.RestApi.model.common.AuthCreateUserRequest;
import com.example.RestApi.model.common.AuthLoginRequest;
import com.example.RestApi.model.common.AuthResponse;
import com.example.RestApi.Exceptions.EmailAlreadyExistsException;
import com.example.RestApi.model.entity.UserEntity;
import com.example.RestApi.Services.AuthService;
import com.example.RestApi.Services.UserDBService;
import com.example.RestApi.handler.AuthHandler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component(value = "authHandler")
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class AuthHandlerImpl implements AuthHandler {

    private final AuthService authService;
    private final UserDBService userDBService;
    private final JWTUtil jwtUtil;

    @Override
    public AuthResponse createUser(AuthCreateUserRequest request) {
        if (userDBService.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("El email ya está en uso.");
        }
        UserDTO newUser = userDBService.createUser(request);
        logUserAction("CREATE", "Se ha creado un nuevo usuario: " + newUser.getUsername(), newUser);

        log.info("Nuevo usuario creado: {} (ID: {})", newUser.getUsername(), newUser.getId());
        return authService.generateAuthResponse(newUser, "User Created Successfully");
    }

    @Override
    public AuthResponse loginUser(AuthLoginRequest request) {
        LoginUserCommonDto commonDto = new LoginUserCommonDto();
        commonDto.setUsername(request.username());
        commonDto.setPassword(request.password());

        // Obtener detalles del usuario
        UserDetails userDetails = fetchUserDetails(commonDto);

        // Autenticar usuario
        Authentication authentication = authService.authenticate(userDetails.getUsername(), commonDto.getPassword());

        // Establecer el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generar token de acceso
        commonDto.setAccessToken(jwtUtil.createToken(authentication));

        // Registrar log de inicio de sesión
        UserDTO performedBy = userDBService.findAuthenticatedUser();
        logUserAction("LOGIN", "El usuario ha iniciado sesión", performedBy);

        // Devolver respuesta de autenticación
        return authService.generateAuthResponse(commonDto.getUserDto(), "User logged successfully");
    }

    private void logUserAction(String action, String description, UserDTO user) {
        try {
            AuditLogDto auditLogDto = new AuditLogDto(
                    null,
                    action,
                    "User",
                    user.getId(),
                    description,
                    user.getUsername(),
                    LocalDateTime.now()
            );
            userDBService.saveAuthenticatedUserLog(auditLogDto);

            log.info("Audit log registered: {} - {} (ID: {}) by {}",
                    auditLogDto.action(), auditLogDto.entity(),
                    auditLogDto.entityId(), auditLogDto.performedBy());
        } catch (Exception e) {
            log.error("Error registering audit log: {}", e.getMessage(), e);
        }
    }

    private UserDetails fetchUserDetails(LoginUserCommonDto commonDto) {
        UserDTO userDto = userDBService.findUserByUsername(commonDto.getUsername());
        commonDto.setUserDto(userDto);
        return authService.buildUserDetails(userDto);
    }
}
