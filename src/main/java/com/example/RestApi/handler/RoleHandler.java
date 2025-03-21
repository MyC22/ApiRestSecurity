package com.example.RestApi.handler;

import com.example.RestApi.model.dto.RoleDTO;
import com.example.RestApi.model.dto.UserDTO;

import java.util.List;

public interface RoleHandler {

    List<RoleDTO> getAllRoles();

    RoleDTO getRoleById(Long id);

    UserDTO addRoleToUser(Long userId, List<String> roleNames);

    UserDTO removeRoleFromUser(Long userId, List<String> roleNames);
}
