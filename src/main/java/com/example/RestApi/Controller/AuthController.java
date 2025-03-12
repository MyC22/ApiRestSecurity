package com.example.RestApi.Controller;

import com.example.RestApi.Controller.RecordDTO.AuthCreateUserRequest;
import com.example.RestApi.Controller.RecordDTO.AuthLoginRequest;
import com.example.RestApi.Controller.RecordDTO.AuthResponse;
import com.example.RestApi.Services.Handlers.AuthHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthHandler authHandler; // 🔹 Se usa AuthHandler en lugar de AuthService

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody @Validated AuthLoginRequest userRequest) {
        return new ResponseEntity<>(authHandler.loginUser(userRequest), HttpStatus.OK);
    }

    @PostMapping("/sign-up") // 🔹 Corregí "sing-up" a "sign-up"
    public ResponseEntity<AuthResponse> register(@RequestBody AuthCreateUserRequest authCreateUser) {
        return new ResponseEntity<>(authHandler.createUser(authCreateUser), HttpStatus.CREATED);
    }
}
