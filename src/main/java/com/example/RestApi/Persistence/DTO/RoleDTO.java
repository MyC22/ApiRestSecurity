package com.example.RestApi.Persistence.DTO;

import com.example.RestApi.Persistence.entity.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDTO {

    private Long id;
    private RoleEnum roleName; // 🔹 Cambiado de String a RoleEnum

    @Builder.Default
    private Set<PermissionDTO> permissionList = new HashSet<>();
}
