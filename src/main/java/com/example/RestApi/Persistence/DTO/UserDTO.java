package com.example.RestApi.Persistence.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private int prioridad;
    private LocalDateTime disableTimestamp;
    private boolean isEnabled;
    private boolean accountNoExpired;
    private boolean accountNoLocked;
    private boolean credentialNoExpired;

    private Set<String> roles;
    private Set<String> permissions;  // 🔹 Nueva propiedad para permisos
}
