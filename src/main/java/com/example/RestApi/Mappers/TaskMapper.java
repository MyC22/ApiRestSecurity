package com.example.RestApi.Mappers;

import com.example.RestApi.model.dto.TaskDTO;
import com.example.RestApi.model.entity.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    // Este mapeo ya no tiene la línea que mapea el userId
    TaskDTO toDto(TaskEntity entity);

    TaskEntity toEntity(TaskDTO dto);
}
