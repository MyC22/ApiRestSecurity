package com.example.RestApi.Services;

import com.example.RestApi.Exceptions.RoleAlreadyAssignedException;
import com.example.RestApi.Exceptions.RoleNotAssignedException;
import com.example.RestApi.Persistence.DTO.RoleDTO;
import com.example.RestApi.Persistence.DTO.UserDTO;
import com.example.RestApi.Persistence.Repository.*;
import com.example.RestApi.Persistence.entity.RoleEntity;
import com.example.RestApi.Persistence.entity.RoleEnum;
import com.example.RestApi.Persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<RoleDTO> getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDTO);
    }

    @Transactional
    public Optional<UserDTO> addRoleToUser(Long userId, List<String> roleNames) {
        UserEntity user = getUserById(userId);
        Set<RoleEntity> newRoles = getRolesFromNames(roleNames);

        if (user.getRoles().containsAll(newRoles)) {
            throw new RoleAlreadyAssignedException("The user already has these roles assigned.");
        }

        user.getRoles().addAll(newRoles);
        UserEntity updatedUser = userRepository.save(user);

//        logAudit("ASSIGN_ROLE", "User", user.getId());
        updateUserAuthorities(updatedUser);

        return Optional.of(userMapper.toDTO(updatedUser));
    }

    @Transactional
    public Optional<UserDTO> removeRoleFromUser(Long userId, List<String> roleNames) {
        UserEntity user = getUserById(userId);
        Set<RoleEntity> rolesToRemove = getRolesFromNames(roleNames);

        if (!user.getRoles().containsAll(rolesToRemove)) {
            throw new RoleNotAssignedException("The specified roles are not assigned to the user");
        }

        user.getRoles().removeAll(rolesToRemove);
        UserEntity updatedUser = userRepository.save(user);

//        logAudit("REMOVE_ROLE", "User", user.getId());
        updateUserAuthorities(updatedUser);

        return Optional.of(userMapper.toDTO(updatedUser));
    }

    private UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Set<RoleEntity> getRolesFromNames(List<String> roleNames) {
        List<RoleEnum> roleEnums = roleNames.stream()
                .map(roleName -> {
                    try {
                        return RoleEnum.valueOf(roleName);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Role " + roleName + " does not exist");
                    }
                })
                .collect(Collectors.toList());

        Set<RoleEntity> roles = new HashSet<>(roleRepository.findRoleEntitiesByRoleNameIn(roleEnums));
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("The specified roles do not exist");
        }

        return roles;
    }

//    private void logAudit(String action, String entity, Long entityId) {
//        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        UserEntity performedBy = userRepository.findUserEntityByUsername(authenticatedUsername)
//                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
//
//        try {
//            auditLogService.registerAudit(action, entity, entityId, performedBy);
//        } catch (Exception e) {
//            log.error("Failed to log audit for {} - {} (ID: {}): {}", action, entity, entityId, e.getMessage(), e);
//        }
//    }

    private void updateUserAuthorities(UserEntity user) {
        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<>();

        user.getRoles().forEach(role ->
                updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name())));

        user.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> updatedAuthorities.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails updatedUserDetails = new User(user.getUsername(), user.getPassword(), updatedAuthorities);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails, authentication.getCredentials(), updatedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}
