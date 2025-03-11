package com.example.RestApi.Services;

import com.example.RestApi.Controller.dto.AuthCreateUserRequest;
import com.example.RestApi.Controller.dto.AuthLoginRequest;
import com.example.RestApi.Controller.dto.AuthResponse;
import com.example.RestApi.Exceptions.EmailAlreadyExistsException;
import com.example.RestApi.Persistence.DTO.UserDTO;
import com.example.RestApi.Persistence.Repository.RoleRepository;
import com.example.RestApi.Persistence.Repository.UserMapper;
import com.example.RestApi.Persistence.Repository.UserRepository;
import com.example.RestApi.Persistence.entity.RoleEntity;
import com.example.RestApi.Persistence.entity.RoleEnum;
import com.example.RestApi.Persistence.entity.UserEntity;
import com.example.RestApi.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl  {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> userMapper.toDTO(user)) // Conversión sin ::
                .collect(Collectors.toList());
    }

    public Optional<UserEntity> getUserById(Long id){
        return userRepository.findById(id);
    }

public boolean disableUserById(Long id) {
    Optional<UserEntity> userOptional = userRepository.findById(id);
    if (userOptional.isPresent()) {
        UserEntity user = userOptional.get();

        // Elimina la relación entre el usuario y los roles, pero no elimina los roles en sí
        user.getRoles().clear();

        // Desactivar el usuario
        user.setEnabled(false);
        user.setDisableTimestamp(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }
    return false;
}


    @Scheduled(cron = "0 0/1 * * * ?")  // Ejecuta cada minuto
    public void deleteInactiveUsers() {
        List<UserEntity> inactiveUsers = userRepository.findByIsEnabledFalse();  // Usuarios deshabilitados
        LocalDateTime now = LocalDateTime.now();

        for (UserEntity user : inactiveUsers) {
            if (user.getDisableTimestamp() != null) {
                // Si han pasado más de 2 minutos desde la desactivación, eliminar usuario
                if (Duration.between(user.getDisableTimestamp(), now).toMinutes() >= 2) {
                    userRepository.delete(user);
                }
            }
        }
    }










}
