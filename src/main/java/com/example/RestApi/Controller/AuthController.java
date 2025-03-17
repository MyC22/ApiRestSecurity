package com.example.RestApi.Controller;


import com.example.RestApi.model.common.AuthCreateUserRequest;
import com.example.RestApi.model.common.AuthLoginRequest;
import com.example.RestApi.model.common.AuthResponse;
import com.example.RestApi.Utils.JWTUtil;
import com.example.RestApi.handler.impl.AuthHandlerImpl;
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
    AuthHandlerImpl authHandler;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody @Validated AuthLoginRequest userRequest){
        return new ResponseEntity<>(this.authHandler.loginUser(userRequest), HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthCreateUserRequest authCreateUser){
        return new ResponseEntity<>(this.authHandler.createUser(authCreateUser), HttpStatus.CREATED);
    }
}
