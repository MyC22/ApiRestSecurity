package com.example.RestApi.Controller;


import com.example.RestApi.Controller.dto.AuthCreateUserRequest;
import com.example.RestApi.Controller.dto.AuthLoginRequest;
import com.example.RestApi.Controller.dto.AuthResponse;
import com.example.RestApi.Services.UserDetailServiceImpl;
import com.example.RestApi.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    UserDetailServiceImpl userDetailService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody @Validated AuthLoginRequest userRequest){
        return new ResponseEntity<>(this.userDetailService.loginUser(userRequest), HttpStatus.OK);

//        UserModel userLogged = userService.getUserByCredentials(user);
//        if(userLogged != null){
//            return jwtUtil.create(String.valueOf(userLogged.getId()), userLogged.getEmail());
//        }
//        return "FAIL";
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthCreateUserRequest authCreateUser){
        return new ResponseEntity<>(this.userDetailService.createUser(), HttpStatus.CREATED);
//    }
}
