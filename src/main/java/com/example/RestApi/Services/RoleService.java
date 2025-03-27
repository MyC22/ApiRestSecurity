package com.example.RestApi.Services;

import com.example.RestApi.Enums.RoleEnum;
import com.example.RestApi.Exceptions.RoleAlreadyAssignedException;
import com.example.RestApi.Exceptions.RoleNotAssignedException;
import com.example.RestApi.model.entity.RoleEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {


    public void validateRolesNotAssigned(Set<RoleEntity> existingRoles, Set<RoleEntity> newRoles) {
        List<String> duplicateRoles = newRoles.stream()
                .map(role -> role.getRoleName().name())
                .filter(roleName -> existingRoles.stream()
                        .map(role -> role.getRoleName().name())
                        .anyMatch(roleName::equals))
                .toList();

        if (!duplicateRoles.isEmpty()) {
            throw new RoleAlreadyAssignedException("El usuario ya tiene los siguientes roles: " + duplicateRoles);
        }
    }


     //Valida si los roles a eliminar están realmente asignados al usuario.
     //Lanza una excepción si algún rol no está asignado.

    public void validateRolesAssigned(Set<RoleEntity> existingRoles, Set<RoleEntity> rolesToRemove) {
        List<String> missingRoles = rolesToRemove.stream()
                .map(role -> role.getRoleName().name())
                .filter(roleName -> existingRoles.stream()
                        .map(role -> role.getRoleName().name())
                        .noneMatch(roleName::equals))
                .toList();

        if (!missingRoles.isEmpty()) {
            throw new RoleNotAssignedException("El usuario no tiene los siguientes roles: " + missingRoles);
        }
    }


     //Convierte una lista de nombres de roles en una lista de RoleEnum.
    public List<RoleEnum> convertToRoleEnums(List<String> roleNames) {
        return roleNames.stream()
                .map(RoleEnum::valueOf)
                .collect(Collectors.toList());
    }
}
