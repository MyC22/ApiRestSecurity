package com.example.RestApi.Services;

import com.example.RestApi.Enums.RoleEnum;
import com.example.RestApi.Mappers.AuditLogMapper;
import com.example.RestApi.Mappers.UserMapper;
import com.example.RestApi.model.dto.AuditLogDto;
import com.example.RestApi.model.common.AuthCreateUserRequest;
import com.example.RestApi.model.dto.UserDTO;
import com.example.RestApi.Repository.AuditLogRepository;
import com.example.RestApi.Repository.RoleRepository;
import com.example.RestApi.Repository.UserRepository;
import com.example.RestApi.model.entity.RoleEntity;
import com.example.RestApi.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserEntity createUser(AuthCreateUserRequest request) {
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

        return userRepository.save(user);
    }



    public UserDTO findUserByUsername(String username) {

        Optional<UserEntity> entity = userRepository.findUserEntityByUsername(username);

        if (entity.isPresent())
            return userMapper.toDTO(entity.get());
        else
            throw new UsernameNotFoundException("El usuario " + username + " no existe");
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


    public void saveAuthenticatedUserLog(AuditLogDto auditLogDto) {
        auditLogRepository.save(auditLogMapper.mapAuditLogDtoToEntity(auditLogDto));
    }
}
