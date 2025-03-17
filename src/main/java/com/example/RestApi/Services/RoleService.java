package com.example.RestApi.Services;

import com.example.RestApi.Exceptions.RoleAlreadyAssignedException;
import com.example.RestApi.Exceptions.RoleNotAssignedException;
import com.example.RestApi.model.dto.RoleDTO;
import com.example.RestApi.model.dto.UserDTO;
import com.example.RestApi.Mappers.PermissionMapper;
import com.example.RestApi.Mappers.RoleMapper;
import com.example.RestApi.Mappers.UserMapper;
import com.example.RestApi.model.entity.RoleEntity;
import com.example.RestApi.Enums.RoleEnum;
import com.example.RestApi.model.entity.UserEntity;
import com.example.RestApi.Repository.RoleRepository;
import com.example.RestApi.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserDetailServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private AuditLogService auditLogService;

    // 🔹 Obtener todos los roles
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 🔹 Obtener un rol por su ID
    public Optional<RoleDTO> getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDTO);
    }

    public Optional<UserDTO> addRoleToUser(Long userId, List<String> roleNames) {
        Optional<UserEntity> userOptional = userService.getUserById(userId);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();

            // Convertir nombres de roles a RoleEnum
            List<RoleEnum> roleEnums = convertToRoleEnums(roleNames);

            // Buscar los roles en la base de datos
            Set<RoleEntity> newRoles = new HashSet<>(roleRepository.findRoleEntitiesByRoleNameIn(roleEnums));

            if (newRoles.isEmpty()) {
                throw new IllegalArgumentException("The specified roles do not exist");
            }

            // Verificar si ya tiene los roles
            if (user.getRoles().containsAll(newRoles)) {
                throw new RoleAlreadyAssignedException("The user already has these roles assigned.");
            }

            // Agregar los nuevos roles sin reemplazar los existentes
            user.getRoles().addAll(newRoles);

            // Guardar cambios
            UserEntity updatedUser = userRepository.save(user);

            // 🔥 REGISTRAR AUDITORÍA PARA CADA ROL AGREGADO
            for (RoleEntity role : newRoles) {
                auditLogService.logUserAction("ADD_ROLE", userId, user.getUsername(),
                        "Asignó el rol '" + role.getRoleName() + "'");
            }

            // 🔹 🔥 ACTUALIZAR LA SESIÓN DEL USUARIO
            updateUserAuthorities(updatedUser);

            return Optional.of(userMapper.toDTO(updatedUser));
        }

        return Optional.empty();
    }




    public Optional<UserDTO> removeRoleFromUser(Long userId, List<String> roleNames) {
        Optional<UserEntity> userOptional = userService.getUserById(userId);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();

            List<RoleEnum> roleEnums = convertToRoleEnums(roleNames);

            Set<RoleEntity> rolesToRemove = user.getRoles().stream()
                    .filter(role -> roleEnums.contains(role.getRoleName()))
                    .collect(Collectors.toSet());

            if (rolesToRemove.isEmpty()) {
                throw new RoleNotAssignedException("The specified roles are not assigned to the user");
            }

            // 🔹 Eliminar roles
            user.getRoles().removeAll(rolesToRemove);

            // Guardar cambios en la base de datos
            UserEntity updatedUser = userRepository.save(user);

            // 🔥 REGISTRAR AUDITORÍA PARA CADA ROL ELIMINADO
            for (RoleEntity role : rolesToRemove) {
                auditLogService.logUserAction("REMOVE_ROLE", userId, user.getUsername(),
                        "Eliminó el rol '" + role.getRoleName() + "'");
            }

            // 🔥 🔹 ACTUALIZAR LA SESIÓN DEL USUARIO
            updateUserAuthorities(updatedUser);

            return Optional.of(userMapper.toDTO(updatedUser));
        }

        return Optional.empty();
    }




    private void updateUserAuthorities(UserEntity user) {
        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<>();

        // Agregar roles actualizados
        user.getRoles().forEach(role ->
                updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name())));

        // Agregar permisos actualizados
        user.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> updatedAuthorities.add(new SimpleGrantedAuthority(permission.getName())));

        // Obtener la autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Crear nueva autenticación con los roles actualizados
            UserDetails updatedUserDetails = new User(user.getUsername(), user.getPassword(), updatedAuthorities);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails, authentication.getCredentials(), updatedAuthorities);

            // Reemplazar la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

    // Convertir nombres de roles a RoleEnum
    private List<RoleEnum> convertToRoleEnums(List<String> roleNames) {
        return roleNames.stream()
                .map(roleName -> {
                    try {
                        return RoleEnum.valueOf(roleName);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Role " + roleName + " does not exist");
                    }
                })
                .collect(Collectors.toList());
    }



}
