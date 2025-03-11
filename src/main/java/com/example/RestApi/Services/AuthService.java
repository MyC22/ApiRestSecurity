package com.example.RestApi.Services;

import com.example.RestApi.Controller.dto.AuthCreateUserRequest;
import com.example.RestApi.Controller.dto.AuthLoginRequest;
import com.example.RestApi.Controller.dto.AuthResponse;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;



    public AuthResponse createUser(AuthCreateUserRequest authCreateUserRequest){

        if (userRepository.existsByEmail(authCreateUserRequest.email())) {
            throw new EmailAlreadyExistsException("The email " + authCreateUserRequest.email() + " is already in use.");
        }

        String username = authCreateUserRequest.username();
        String password = authCreateUserRequest.password();
        String email = authCreateUserRequest.email();
        int prioridad = authCreateUserRequest.prioridad();

        List<String> roleNames = authCreateUserRequest.roleRequest().roleListName();

        // Convertir los nombres de roles a RoleEnum
        List<RoleEnum> roleEnums = roleNames.stream()
                .map(roleName -> {
                    try {
                        return RoleEnum.valueOf(roleName);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Role " + roleName + " does not exist");
                    }
                })
                .collect(Collectors.toList());

        // Buscar los roles en la base de datos
        Set<RoleEntity> roleEntitySet = new HashSet<>(roleRepository.findRoleEntitiesByRoleNameIn(roleEnums));

        if (roleEntitySet.isEmpty()) {
            throw new IllegalArgumentException("The roles specified do not exist");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .prioridad(prioridad)
                .roles(roleEntitySet)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .isEnabled(true)
                .build();

        // Guardar usuario
        UserEntity userCreated = userRepository.save(userEntity);

        // Crear lista de permisos
        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userCreated.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        // Autenticar usuario
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userCreated.getUsername(), userCreated.getPassword(), authorityList);

        // Generar token
        String accessToken = jwtUtil.createToken(authentication);

        // Respuesta de autenticación
        return new AuthResponse(
                userCreated.getId(),
                userCreated.getUsername(),
                "User Created Successfully",
                userEntity.getEmail(),
                userEntity.getPrioridad(),
                accessToken,
                true);
    }

    //find user in database
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe"));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // Agregar roles
        userEntity.getRoles().forEach(role ->
                authorityList.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name()))); // 🔹 Se usa name() de RoleEnum

        // Agregar permisos
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

    //Login user
    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.createToken(authentication);

        // Retrieve user ID from the database
        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe"));

        // Make sure userId is included here
        return new AuthResponse(userEntity.getId(), userEntity.getUsername(), "User Logged successfully", userEntity.getEmail(), userEntity.getPrioridad(), accessToken, true);
    }

    //authentication method
    public Authentication authenticate(String username, String password){
        UserDetails userDetails = this.loadUserByUsername(username);

        if(userDetails == null){
            throw new BadCredentialsException("Invalid username or password");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid Password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
