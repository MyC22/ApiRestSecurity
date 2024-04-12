package com.example.RestApi.Controller;


import com.example.RestApi.Model.UserModel;
import com.example.RestApi.Services.UserService;
import com.example.RestApi.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("api/login")
    public String login(@RequestBody UserModel user){
        UserModel userLogged = userService.getUserByCredentials(user);
        if(userLogged != null){
            return jwtUtil.create(String.valueOf(userLogged.getId()), userLogged.getEmail());

        }
        return "FAIL";

    }
}
