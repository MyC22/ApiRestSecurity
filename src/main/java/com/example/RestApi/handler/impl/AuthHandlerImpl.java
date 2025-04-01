package com.example.RestApi.handler.impl;

import com.example.RestApi.Enums.RoleEnum;
import com.example.RestApi.Exceptions.InvalidLoginException;
import com.example.RestApi.Utils.JWTUtil;
import com.example.RestApi.model.common.LoginUserCommonDto;
import com.example.RestApi.model.dto.UserDTO;
import com.example.RestApi.model.common.AuthCreateUserRequest;
import com.example.RestApi.model.common.AuthLoginRequest;
import com.example.RestApi.model.common.AuthResponse;
import com.example.RestApi.Exceptions.EmailAlreadyExistsException;
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
            userDBService.logUserAction("CREATE", null, null,"Intento Fallido: Email ya en uso", false, true);
            throw new EmailAlreadyExistsException("El email ya está en uso.");
        }
        UserDTO newUser = userDBService.createUser(request);
        boolean isCritical = newUser.getRoles().contains(RoleEnum.ADMIN) || newUser.getRoles().contains(RoleEnum.DEVELOPER);
        userDBService.logUserAction("CREATE", newUser, null,
                "Se ha creado un nuevo usuario: " + newUser.getUsername(), true, isCritical);
        log.info("Nuevo usuario creado: {} (ID: {})", newUser.getUsername(), newUser.getId());
        return authService.generateAuthResponse(newUser, "User Created Successfully");
    }


    @Override
    public AuthResponse loginUser(AuthLoginRequest request) {
        LoginUserCommonDto commonDto = new LoginUserCommonDto();
        commonDto.setUsername(request.username());
        commonDto.setPassword(request.password());
        try {
            // Obtener detalles del usuario
            UserDetails userDetails = fetchUserDetails(commonDto);
            // Autenticar usuario con los detalles ya obtenidos
            Authentication authentication = authService.authenticate(userDetails, commonDto.getPassword());
            // Establecer el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Generar token de acceso
            commonDto.setAccessToken(jwtUtil.createToken(authentication));
            // Registrar log de inicio de sesión exitoso
            UserDTO performedBy = userDBService.findAuthenticatedUser();
            userDBService.logUserAction("LOGIN", performedBy, null, "Inicio de sesión exitoso", true);
            // Devolver respuesta de autenticación
            return authService.generateAuthResponse(commonDto.getUserDto(), "User logged successfully");
        } catch (Exception e) {
            userDBService.logUserAction("LOGIN", null, null,
                    "Intento fallido de inicio de sesión para el usuario: " + request.username(), false, true);
            throw new InvalidLoginException("Credenciales inválidas. Por favor, verifica tu usuario y contraseña.");
        }
    }


    private UserDetails fetchUserDetails(LoginUserCommonDto commonDto) {
        UserDTO userDto = userDBService.findUserByUsername(commonDto.getUsername());
        commonDto.setUserDto(userDto);
        return authService.buildUserDetails(userDto);
    }
}
