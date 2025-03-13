package com.example.RestApi.model.dto;

import com.example.RestApi.enums.RoleEnum;
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
public class RoleDto {

    private Long id;
    private RoleEnum roleName; // 🔹 Cambiado de String a RoleEnum

    @Builder.Default
    private Set<PermissionDto> permissionList = new HashSet<>();
}
