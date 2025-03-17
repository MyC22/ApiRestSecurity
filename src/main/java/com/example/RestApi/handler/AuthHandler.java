package com.example.RestApi.handler;

import com.example.RestApi.model.common.AuthCreateUserRequest;
import com.example.RestApi.model.common.AuthLoginRequest;
import com.example.RestApi.model.common.AuthResponse;


public interface AuthHandler {

    AuthResponse createUser(AuthCreateUserRequest request);
    AuthResponse loginUser(AuthLoginRequest request);
}
