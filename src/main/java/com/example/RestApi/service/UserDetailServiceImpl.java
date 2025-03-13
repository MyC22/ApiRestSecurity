package com.example.RestApi.service;

import com.example.RestApi.model.dto.NotUserDto;
import com.example.RestApi.model.entity.UserEntity;
import com.example.RestApi.repository.UserMapper;
import com.example.RestApi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    public List<NotUserDto> getUsers() {
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
