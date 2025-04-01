package com.example.RestApi.handler;

import com.example.RestApi.model.dto.TaskDTO;

public interface TaskHandler {
    TaskDTO assignTaskToUser(Long userId, TaskDTO taskDTO);

}
