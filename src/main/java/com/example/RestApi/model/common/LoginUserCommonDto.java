package com.example.RestApi.model.common;

import com.example.RestApi.model.dto.UserDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUserCommonDto implements Serializable {

    private String username;
    private String password;

    private UserDTO userDto;
    private String accessToken;
}
