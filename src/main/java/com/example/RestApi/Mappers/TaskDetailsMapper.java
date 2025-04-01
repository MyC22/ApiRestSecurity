package com.example.RestApi.Mappers;

import com.example.RestApi.model.dto.TaskDetailsDTO;
import com.example.RestApi.model.entity.TaskDetailsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskDetailsMapper {

    TaskDetailsDTO toDto(TaskDetailsEntity entity);

    TaskDetailsEntity toEntity(TaskDetailsDTO dto);
}
