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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        UserEntity newUser = userDBService.createUser(request); // Se crea el usuario a través de UserDBService
        return authService.generateAuthResponse(newUser, "User Created Successfully");
    }

    @Override
    public AuthResponse loginUser(AuthLoginRequest request) {
        LoginUserCommonDto commonDto = new LoginUserCommonDto();
        commonDto.setUsername(request.username());
        commonDto.setPassword(request.password());

        //Recuperar los detalles del usuario desde DBService
        UserDetails userDetails = fetchUserDetails(commonDto);

        //Autenticar al usuario
        Authentication authentication = authService.authenticate(userDetails.getUsername(), commonDto.getPassword());

        //Establecer el SecurityContextHolder después de la autenticación
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Generar el token de acceso
        createUserAccessToken(authentication, commonDto);

        //Devolver la respuesta con datos del usuario y el token
        return new AuthResponse(
                commonDto.getUserDto().getId(),
                commonDto.getUserDto().getUsername(),
                "User logged successfully",
                commonDto.getUserDto().getEmail(),
                commonDto.getUserDto().getPrioridad(),
                commonDto.getAccessToken(),
                true
        );
    }

    private void createUserAccessToken(Authentication authentication, LoginUserCommonDto commonDto) {
        try {
            commonDto.setAccessToken(jwtUtil.createToken(authentication));
            UserDTO performedBy = userDBService.findAuthenticatedUser();

            AuditLogDto auditLogDto = new AuditLogDto();
            auditLogDto.setAction("LOGIN");
            auditLogDto.setEntity(RoleEnum.USER.label);
            auditLogDto.setEntityId(performedBy.getId());
            auditLogDto.setTimestamp(LocalDateTime.now());

            userDBService.saveAuthenticatedUserLog(auditLogDto);
            log.info("Audit log registered: {} - {} (ID: {}) by {}",
                    auditLogDto.getAction(), auditLogDto.getEntity(),
                    auditLogDto.getEntityId(), performedBy.getUsername());
        } catch (Exception e) {
            log.error("Error registering audit log: {}", e.getMessage(), e);
        }
    }

//
private UserDetails fetchUserDetails(LoginUserCommonDto commonDto) {
    UserDTO userDto = userDBService.findUserByUsername(commonDto.getUsername());

    List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

    //Agregar roles con prefijo "ROLE_"
    userDto.getRoles().forEach(role ->
            authorityList.add(new SimpleGrantedAuthority("ROLE_" + role))
    );

    //Agregar permisos directamente
    userDto.getPermissions().forEach(permission ->
            authorityList.add(new SimpleGrantedAuthority(permission))
    );

    commonDto.setUserDto(userDto);

    return new User(userDto.getUsername(),
            userDto.getPassword(),
            userDto.isEnabled(),
            userDto.isAccountNoExpired(),
            userDto.isCredentialNoExpired(),
            userDto.isAccountNoLocked(),
            authorityList);
}


}
