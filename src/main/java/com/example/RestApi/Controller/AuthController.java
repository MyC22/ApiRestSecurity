package com.example.RestApi.Controller;


import com.example.RestApi.Model.UserModel;
import com.example.RestApi.Services.UserService;
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

    @PostMapping("api/login")
    public String login(@RequestBody UserModel user){
        String email = user.getEmail();
        String password = user.getPassword();

        UserModel userAuth = userService.verifyUser(email, password);
        if(userAuth != null){
          return "Usuario autenticado correctamente";
        }else {
            return "Credenciales incorrectas";
        }
    }
}
