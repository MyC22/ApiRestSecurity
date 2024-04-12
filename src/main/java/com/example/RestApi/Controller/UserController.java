package com.example.RestApi.Controller;

import com.example.RestApi.Model.UserModel;
import com.example.RestApi.Services.UserService;
import com.example.RestApi.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    JWTUtil jwtUtil;

    @GetMapping("/users")
    public ArrayList<UserModel> getUsers(@RequestHeader(value = "Authorization") String token){
        if(!valiteToken(token)){
          return null;
        }
        return userService.getUsers();
    }

    private boolean valiteToken(String token){
        String userId = jwtUtil.getKey(token); //obtener id del usuario
        return userId != null; //si el usuario no es nullo va bien
    }

    @PostMapping("/saveUsers")
    public UserModel saveUser(@RequestBody UserModel user){
        return this.userService.saveUser(user);
    }

    @PostMapping("/register")
    public UserModel registerUser(@RequestBody UserModel user){
        return this.userService.registerUser(user);
    }

    @GetMapping(path = "/user/{id}")
    public Optional<UserModel> getUserById(@PathVariable("id") Long id){
        return this.userService.getUserById(id);
    }

    @GetMapping("/query")
    public ArrayList<UserModel> getUsersByPriority(@RequestParam("priority") Integer priority){
        return this.userService.getUserByPriority(priority);
    }

    @DeleteMapping(path = "/delete/{id}")
    public void deleteUserById(@RequestHeader(value = "Authorization") String token , @PathVariable("id") Long id){
        if(!valiteToken(token)){return;}
        userService.deleteUserById(id);

    }
}
