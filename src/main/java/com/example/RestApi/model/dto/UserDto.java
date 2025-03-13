package com.example.RestApi.model.dto;

import com.example.RestApi.model.entity.RoleEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String email;
    private int prioridad;
    private LocalDateTime disableTimestamp;
    private boolean isEnabled;
    private boolean accountNoExpired;
    private boolean accountNoLocked;
    private boolean credentialNoExpired;
    private Set<RoleEntity> roles = new HashSet<>();
}
