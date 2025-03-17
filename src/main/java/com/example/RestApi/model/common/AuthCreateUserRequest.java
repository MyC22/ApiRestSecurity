package com.example.RestApi.model.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record AuthCreateUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String email,
        @NotBlank int prioridad,
        @Valid AuthCreateRoleRequest roleRequest
) {
    public AuthCreateUserRequest {
        if (prioridad <= 0) {
            prioridad = 1;
        }
        if (roleRequest == null) {
            roleRequest = new AuthCreateRoleRequest(List.of("USER")); // Valor por defecto si es null
        }
    }
}


