package com.example.RestApi.Services;

import com.example.RestApi.Enums.RoleEnum;
import com.example.RestApi.Exceptions.RoleAlreadyAssignedException;
import com.example.RestApi.Exceptions.RoleNotAssignedException;
import com.example.RestApi.Mappers.AuditLogMapper;
import com.example.RestApi.Mappers.RoleMapper;
import com.example.RestApi.Mappers.UserMapper;
import com.example.RestApi.model.common.AuthCreateUserRequest;
import com.example.RestApi.model.dto.AuditLogDto;
import com.example.RestApi.model.dto.RoleDTO;
import com.example.RestApi.model.dto.UserDTO;
import com.example.RestApi.Repository.AuditLogRepository;
import com.example.RestApi.Repository.RoleRepository;
import com.example.RestApi.Repository.UserRepository;
import com.example.RestApi.model.entity.RoleEntity;
import com.example.RestApi.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class UserDBService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private AuditLogRepository auditLogRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    private AuditLogMapper auditLogMapper;
    private RoleMapper roleMapper;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<UserEntity> getUserById(Long id){
        return userRepository.findById(id);
    }

    public Optional<UserDTO> saveUser(UserEntity user) {
        UserEntity savedUser = userRepository.save(user);
        return Optional.of(userMapper.toDTO(savedUser));
    }

    public UserDTO convertToDTO(UserEntity userEntity) {
        return userMapper.toDTO(userEntity);
    }



    public UserDTO createUser(AuthCreateUserRequest request) {
        Set<RoleEntity> roles = new HashSet<>(roleRepository.findRoleEntitiesByRoleNameIn(
                request.roleRequest().roleListName().stream().map(RoleEnum::valueOf).collect(Collectors.toList())
        ));

        UserEntity user = UserEntity.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .prioridad(request.prioridad())
                .roles(roles)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .isEnabled(true)
                .build();

        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    public UserDTO findUserByUsername(String username) {
        Optional<UserEntity> entity = userRepository.findUserEntityByUsername(username);

        if (entity.isPresent())
            return userMapper.toDTO(entity.get());
        else
            throw new UsernameNotFoundException("El usuario " + username + " no existe");
    }



    //RoleService

    // Obtener todos los roles
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Obtener un rol por su ID
    public Optional<RoleDTO> getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDTO);
    }

    public Set<RoleEntity> findRolesByNames(List<String> roleNames) {
        List<RoleEnum> roleEnums = roleNames.stream()
                .map(RoleEnum::valueOf)
                .toList();
        return new HashSet<>(roleRepository.findRoleEntitiesByRoleNameIn(roleEnums));
    }


    public Optional<UserEntity> addRoleToUser(Long userId, Set<RoleEntity> newRoles) {
        Optional<UserEntity> userOptional = getUserById(userId);
        if (userOptional.isEmpty()) {
            return Optional.empty(); // Usuario no encontrado
        }

        UserEntity user = userOptional.get();


        //Añadir los nuevos roles
        user.getRoles().addAll(newRoles);
        UserEntity updatedUser = userRepository.save(user);

        return Optional.of(updatedUser);
    }


    public Optional<UserEntity> removeRoleFromUser(Long userId, Set<RoleEntity> rolesToRemove) {
        Optional<UserEntity> userOptional = getUserById(userId);
        if (userOptional.isEmpty()) {
            return Optional.empty(); // Usuario no encontrado
        }

        UserEntity user = userOptional.get();


        //Eliminar los roles
        user.getRoles().removeAll(rolesToRemove);
        UserEntity updatedUser = userRepository.save(user);

        return Optional.of(updatedUser);
    }

    public void logUserAction(String action, UserDTO user, Long id, String description, boolean success, boolean isCritical) {
        String performedBy = "SYSTEM";
        try {
            try {
                performedBy = findAuthenticatedUser().getUsername();
            } catch (Exception e) {
                log.warn("No authenticated user found, using 'SYSTEM'.");
            }

            // Construcción del objeto de auditoría
            AuditLogDto auditLogDto = new AuditLogDto(
                    null,
                    action,
                    "User",
                    id != null ? id : (user != null ? user.getId() : null),
                    description,
                    performedBy,
                    LocalDateTime.now(),
                    success ? "SUCCESS" : "FAILURE",
                    isCritical
            );

            // Guardar el registro de auditoría
            saveAuthenticatedUserLog(auditLogDto);

            log.info("Audit log registered: {} - {} (ID: {}) by {} - Status: {}",
                    auditLogDto.action(), auditLogDto.entity(),
                    auditLogDto.entityId(), auditLogDto.performedBy(), auditLogDto.status());

        } catch (Exception e) {
            log.error("Error registering audit log: {}", e.getMessage(), e);
        }
    }

    public void logUserAction(String action, UserDTO user, Long id, String description, boolean success) {
        logUserAction(action, user, id, description, success, false);
    }

    public void saveAuthenticatedUserLog(AuditLogDto auditLogDto) {
        auditLogRepository.save(auditLogMapper.mapAuditLogDtoToEntity(auditLogDto));
    }



    public UserDTO findAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("No authenticated user found in SecurityContext.");
            throw new UsernameNotFoundException("Authenticated user not found");
        }

        String authenticatedUsername = authentication.getName();
        log.info("Authenticated user: {}", authenticatedUsername);

        return userRepository.findUserEntityByUsername(authenticatedUsername)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authenticatedUsername));
    }








}
