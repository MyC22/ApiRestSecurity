package com.example.RestApi.Controller;

import com.example.RestApi.handler.impl.RoleHandlerImpl;
import com.example.RestApi.model.dto.RoleDTO;
import com.example.RestApi.model.dto.UserDTO;
import com.example.RestApi.Services.RoleService;
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
    private RoleHandlerImpl roleHandler;

    // Obtener todos los roles
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleHandler.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        RoleDTO role = roleHandler.getRoleById(id);
        return ResponseEntity.ok(role);
    }


    @PutMapping("/{userId}/add-role")
    public ResponseEntity<UserDTO> addRoleToUser(@PathVariable Long userId, @RequestBody Map<String, List<String>> roleRequest) {
        List<String> roleNames = roleRequest.get("roles");
        UserDTO updatedUser = roleHandler.addRoleToUser(userId, roleNames);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{userId}/remove-role")
    public ResponseEntity<UserDTO> removeRoleFromUser(@PathVariable Long userId, @RequestBody Map<String, List<String>> roleRequest) {
        List<String> roleNames = roleRequest.get("roles");
        UserDTO updatedUser = roleHandler.removeRoleFromUser(userId, roleNames);
        return ResponseEntity.ok(updatedUser);
    }

}
