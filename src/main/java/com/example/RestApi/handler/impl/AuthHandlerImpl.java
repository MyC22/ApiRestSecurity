package com.example.RestApi.handler.impl;

import com.example.RestApi.Utils.JWTUtil;
import com.example.RestApi.enums.RoleEnum;
import com.example.RestApi.exception.EmailAlreadyExistsException;
import com.example.RestApi.handler.AuthHandler;
import com.example.RestApi.model.common.AuthCreateUserRequest;
import com.example.RestApi.model.common.AuthLoginRequest;
import com.example.RestApi.model.common.AuthResponse;
import com.example.RestApi.model.common.LoginUserCommonDto;
import com.example.RestApi.model.dto.AuditLogDto;
import com.example.RestApi.model.dto.UserDto;
import com.example.RestApi.model.entity.UserEntity;
import com.example.RestApi.service.AuthService;
import com.example.RestApi.service.UserDBService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
        commonDto.setUsername(request.password());

        //Step 1 -- Retrieve user details
        UserDetails userDetails = fetchUserDetails(commonDto);

        //Step 2 -- Authenticate user
        Authentication authentication = authService.authenticate(userDetails, commonDto.getPassword());

        //Step 3 -- Create auth token
        createUserAccessToken(authentication, commonDto);

        //Step 4 -- Build and return response
        return new AuthResponse(commonDto.getUserDto().getId(),
                commonDto.getUserDto().getUsername(),
                "User logged successfully",
                commonDto.getUserDto().getEmail(),
                commonDto.getUserDto().getPrioridad(),
                commonDto.getAccessToken(),
                true);

    }

    private void createUserAccessToken(Authentication authentication, LoginUserCommonDto commonDto) {
        try {
            commonDto.setAccessToken(jwtUtil.createToken(authentication));

            UserDto performedBy = userDBService.findAuthenticatedUser();

            AuditLogDto auditLogDto = new AuditLogDto();
            auditLogDto.setAction("LOGIN");
            auditLogDto.setEntity(RoleEnum.USER.label);
            auditLogDto.setEntityId(performedBy.getId());
            auditLogDto.setTimestamp(LocalDateTime.now());

            userDBService.saveAuthenticatedUserLog(auditLogDto);
            log.info("Audit log registered: {} - {} (ID: {}) by {}", auditLogDto.getAction(), auditLogDto.getEntity(), auditLogDto.getEntityId(), performedBy.getUsername());
        } catch (Exception e) {
            log.error("Error registering audit log: {}", e.getMessage(), e);
        }
    }

    private UserDetails fetchUserDetails(LoginUserCommonDto commonDto) {
        UserDto userDto = userDBService.findUserByUsername(commonDto.getUsername());

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userDto.getRoles().forEach(role ->
                authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name())));
        userDto.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())

                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

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
