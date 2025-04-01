package com.example.RestApi.model.dto;

import lombok.Data;

@Data
public class TaskDetailsDTO {
    private Long id;
    private String detailDescription;
    private boolean completed;
}
