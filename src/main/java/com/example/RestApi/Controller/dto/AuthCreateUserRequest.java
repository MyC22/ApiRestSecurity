package com.example.RestApi.Controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record AuthCreateUserRequest(@NotBlank String username,
                             @NotBlank String password,

                             @NotBlank String email,

                             @NotBlank int prioridad,
                             @Valid AuthCreateRoleRequest roleRequest) {

}
