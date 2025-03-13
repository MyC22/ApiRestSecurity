package com.example.RestApi.model.common;

import com.example.RestApi.model.dto.UserDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUserCommonDto implements Serializable {
    private String username;
    private String password;

    private UserDto userDto;
    private String accessToken;

}
