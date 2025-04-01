package com.example.RestApi.handler.impl;

import com.example.RestApi.Services.TaskService;
import com.example.RestApi.Services.UserDBService;
import com.example.RestApi.handler.TaskHandler;
import com.example.RestApi.model.dto.TaskDTO;
import com.example.RestApi.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(value = "taskHandlerImpl")
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class TaskHandlerImpl implements TaskHandler {

    private UserDBService userDBService;
    private TaskService taskService;


    @Override
    public TaskDTO assignTaskToUser(Long userId, TaskDTO taskDTO) {

        taskService.validateTaskBeforeAssignment(taskDTO);

        Optional<UserEntity> user = userDBService.getUserById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con el ID: " + userId);
        }

        return userDBService.assignTaskToUser(userId, taskDTO);
    }
}
