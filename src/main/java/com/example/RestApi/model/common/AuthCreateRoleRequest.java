package com.example.RestApi.model.common;

import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;

//max roles for a user 3
@Validated
public record AuthCreateRoleRequest(
        @Size(
                max = 3, message = "The user cannot have more than 3 roles")
        List<String> roleListName) {

}
