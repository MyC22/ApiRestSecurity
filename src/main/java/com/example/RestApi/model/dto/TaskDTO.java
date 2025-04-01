package com.example.RestApi.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskDTO {

    private Long id;
    private String description;
    private boolean taskStatus;
    private List<TaskDetailsDTO> taskDetails;
}

