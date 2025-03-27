package com.example.RestApi.Services;

import com.example.RestApi.model.dto.UserDTO;
import com.example.RestApi.model.common.AuthResponse;
import com.example.RestApi.Repository.UserRepository;
import com.example.RestApi.model.entity.UserEntity;
import com.example.RestApi.Utils.JWTUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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


    // Método para autenticar usuario con UserDetails
    public Authentication authenticate(UserDetails userDetails, String password) {
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }


    public UserDetails buildUserDetails(UserDTO userDto) {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userDto.getRoles().forEach(role ->
                authorityList.add(new SimpleGrantedAuthority("ROLE_" + role))
        );

        userDto.getPermissions().forEach(permission ->
                authorityList.add(new SimpleGrantedAuthority(permission))
        );

        return new User(
                userDto.getUsername(),
                userDto.getPassword(),
                userDto.isEnabled(),
                userDto.isAccountNoExpired(),
                userDto.isCredentialNoExpired(),
                userDto.isAccountNoLocked(),
                authorityList
        );
    }


    // Metodo para generar la respuesta de autenticacion

    public AuthResponse generateAuthResponse(UserDTO userDTO, String message) {
        // Crear lista de permisos
        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // Agregar roles con prefijo "ROLE_"
        userDTO.getRoles().forEach(role ->
                authorityList.add(new SimpleGrantedAuthority("ROLE_" + role)));

        // Agregar permisos directamente
        userDTO.getPermissions().forEach(permission ->
                authorityList.add(new SimpleGrantedAuthority(permission)));

        // Autenticar usuario
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDTO.getUsername(), userDTO.getPassword(), authorityList);

        // Generar token JWT
        String accessToken = jwtUtil.createToken(authentication);

        // Devolver la respuesta con los datos del usuario
        return new AuthResponse(
                userDTO.getId(),
                userDTO.getUsername(),
                message,
                userDTO.getEmail(),
                userDTO.getPrioridad(),
                accessToken,
                true
        );
    }

}
