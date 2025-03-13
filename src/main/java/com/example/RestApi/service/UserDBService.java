package com.example.RestApi.service;

import com.example.RestApi.mapper.RoleMapper;
import com.example.RestApi.model.common.AuthCreateUserRequest;
import com.example.RestApi.enums.RoleEnum;
import com.example.RestApi.model.dto.AuditLogDto;
import com.example.RestApi.model.dto.NotUserDto;
import com.example.RestApi.model.dto.UserDto;
import com.example.RestApi.model.entity.RoleEntity;
import com.example.RestApi.model.entity.UserEntity;
import com.example.RestApi.repository.AuditLogRepository;
import com.example.RestApi.repository.RoleRepository;
import com.example.RestApi.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
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
    private RoleMapper roleMapper;

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

    public UserDto findUserByUsername(String username) {
        NotUserDto user = new NotUserDto();

        Optional<UserEntity> entity = userRepository.findUserEntityByUsername(username);

        if (entity.isPresent())
            return roleMapper.mapUserEntityToUserDto(entity.get());
        else
            throw new UsernameNotFoundException("El usuario " + username + " no existe");
    }

    public UserDto findAuthenticatedUser() {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> entity = userRepository.findUserEntityByUsername(authenticatedUsername);

        if (entity.isPresent() && StringUtils.isNotEmpty(entity.get().getUsername()))
            return roleMapper.mapUserEntityToUserDto(entity.get());
        else
            throw new UsernameNotFoundException("Authenticated user not found");

    }

    public void saveAuthenticatedUserLog(AuditLogDto auditLogDto) {
        auditLogRepository.save(roleMapper.mapAuditLogDtoToEntity(auditLogDto));
    }
}
