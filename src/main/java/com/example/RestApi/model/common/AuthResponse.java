package com.example.RestApi.model.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"userId", "username", "message", "email", "prioridad", "jwt", "status"})
public record AuthResponse(Long userId, String username, String message, String email, int prioridad, String jwt, boolean status) {

}
