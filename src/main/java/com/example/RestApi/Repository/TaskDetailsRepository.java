package com.example.RestApi.Repository;

import com.example.RestApi.model.entity.TaskDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDetailsRepository extends JpaRepository<TaskDetailsEntity, Long> {
}
