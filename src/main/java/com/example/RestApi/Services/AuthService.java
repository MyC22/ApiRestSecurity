package com.example.RestApi.Services;

import com.example.RestApi.model.dto.UserDTO;
import com.example.RestApi.model.common.AuthLoginRequest;
import com.example.RestApi.model.common.AuthResponse;
import com.example.RestApi.Repository.UserRepository;
import com.example.RestApi.model.entity.UserEntity;
import com.example.RestApi.Utils.JWTUtil;
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

    public AuthService(UserRepository userRepository, JWTUtil jwtUtil,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
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


    // Método para autenticar usuario
    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    // Método para generar la respuesta de autenticación
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
