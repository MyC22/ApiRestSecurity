package com.example.RestApi.Services;

import com.example.RestApi.Persistence.DTO.UserDTO;
import com.example.RestApi.Persistence.Repository.Mappers.UserMapper;
import com.example.RestApi.Persistence.Repository.UserRepository;
import com.example.RestApi.Persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    public UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }










}
