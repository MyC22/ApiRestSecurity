package com.example.RestApi.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "task_details")
public class TaskDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stepDescription;
    private boolean isCompleted; // true = completado, false = pendiente

    // Relación con TaskEntity (Cada detalle pertenece a una tarea principal)
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;
}