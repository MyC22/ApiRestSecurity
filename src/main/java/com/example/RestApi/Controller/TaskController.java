package com.example.RestApi.Controller;

import com.example.RestApi.handler.TaskHandler;
import com.example.RestApi.handler.impl.TaskHandlerImpl;
import com.example.RestApi.model.dto.TaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskHandlerImpl taskHandler;

    @PostMapping("/assign/{userId}")
    public ResponseEntity<TaskDTO> assignTaskToUser(@PathVariable Long userId, @RequestBody TaskDTO taskDTO) {
        TaskDTO assignedTask = taskHandler.assignTaskToUser(userId, taskDTO);
        return new ResponseEntity<>(assignedTask, HttpStatus.CREATED);
    }

}
