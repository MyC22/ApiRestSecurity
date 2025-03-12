package com.example.RestApi.Services.Handlers;

import com.example.RestApi.Controller.RecordDTO.AuthCreateUserRequest;
import com.example.RestApi.Controller.RecordDTO.AuthLoginRequest;
import com.example.RestApi.Controller.RecordDTO.AuthResponse;
import com.example.RestApi.Exceptions.EmailAlreadyExistsException;
import com.example.RestApi.Persistence.entity.UserEntity;
import com.example.RestApi.Services.AuthService;
import com.example.RestApi.Services.UserDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthHandler {

    private final AuthService authService;
    private final UserDBService userDBService;

    public AuthHandler(AuthService authService, UserDBService userDBService) {
        this.authService = authService;
        this.userDBService = userDBService;
    }

    public AuthResponse createUser(AuthCreateUserRequest request) {
        if (userDBService.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("El email ya está en uso.");
        }

        UserEntity newUser = userDBService.createUser(request); // Se crea el usuario a través de UserDBService
        return authService.generateAuthResponse(newUser, "User Created Successfully");
    }

    public AuthResponse loginUser(AuthLoginRequest request) {
        return authService.loginUser(request);
    }
}
