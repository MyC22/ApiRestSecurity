package com.example.RestApi.controller;

import com.example.RestApi.model.dto.RoleDto;
import com.example.RestApi.model.dto.NotUserDto;
import com.example.RestApi.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // Obtener todos los roles
    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    // Obtener un rol por su ID
    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        Optional<RoleDto> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/add-role")
    public ResponseEntity<NotUserDto> addRoleToUser(@PathVariable Long userId, @RequestBody Map<String, List<String>> roleRequest) {
        List<String> roleNames = roleRequest.get("roles");  // Extraer la lista de roles desde el JSON
        Optional<NotUserDto> updatedUser = roleService.addRoleToUser(userId, roleNames);

        return updatedUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/remove-role")
    public ResponseEntity<NotUserDto> removeRoleFromUser(@PathVariable Long userId, @RequestBody Map<String, List<String>> roleRequest) {
        List<String> roleNames = roleRequest.get("roles");  // Extraer la lista de roles desde el JSON
        Optional<NotUserDto> updatedUser = roleService.removeRoleFromUser(userId, roleNames);

        return updatedUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
