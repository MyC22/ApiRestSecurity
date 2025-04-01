package com.example.RestApi.Services;

import com.example.RestApi.model.dto.TaskDTO;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    public void validateTaskBeforeAssignment(TaskDTO taskDTO) {
        if (taskDTO.getDescription() == null || taskDTO.getDescription().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la tarea no puede estar vacía.");
        }
    }
}
