package com.example.RestApi.Services;

import com.example.RestApi.Controller.RecordDTO.AuthCreateUserRequest;
import com.example.RestApi.Controller.RecordDTO.AuthLoginRequest;
import com.example.RestApi.Controller.RecordDTO.AuthResponse;
import com.example.RestApi.Exceptions.EmailAlreadyExistsException;
import com.example.RestApi.Persistence.Repository.RoleRepository;
import com.example.RestApi.Persistence.Repository.UserRepository;
import com.example.RestApi.Persistence.entity.RoleEntity;
import com.example.RestApi.Persistence.entity.RoleEnum;
import com.example.RestApi.Persistence.entity.UserEntity;
import com.example.RestApi.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public AuthService(UserRepository userRepository, JWTUtil jwtUtil,
                       PasswordEncoder passwordEncoder, AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe"));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userEntity.getRoles().forEach(role ->
                authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name())));
        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList);
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.createToken(authentication);
        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe"));

        auditLogService.registerAudit("LOGIN", "USER", userEntity.getId());

        return new AuthResponse(
                userEntity.getId(),
                userEntity.getUsername(),
                "User Logged successfully",
                userEntity.getEmail(),
                userEntity.getPrioridad(),
                accessToken,
                true);
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public AuthResponse generateAuthResponse(UserEntity userEntity, String message) {
        // Crear lista de permisos
        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        // Autenticar usuario
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userEntity.getUsername(), userEntity.getPassword(), authorityList);

        // Generar token JWT
        String accessToken = jwtUtil.createToken(authentication);

        // Devolver la respuesta con los datos del usuario
        return new AuthResponse(
                userEntity.getId(),
                userEntity.getUsername(),
                message,
                userEntity.getEmail(),
                userEntity.getPrioridad(),
                accessToken,
                true
        );
    }

}
