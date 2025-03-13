package com.example.RestApi.controller;

import com.example.RestApi.Utils.JWTUtil;
import com.example.RestApi.model.dto.NotUserDto;
import com.example.RestApi.repository.UserMapper;
import com.example.RestApi.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/method")
public class UserController {

    @Autowired
    UserDetailServiceImpl userService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEVELOPER')")
    public ResponseEntity<List<NotUserDto>> getUsers() {
        List<NotUserDto> notUserDtos = userService.getUsers();
        return ResponseEntity.ok(notUserDtos);
    }


    @GetMapping("/get")
    public String helloGet(){
        return "Hello world - GET";
    }

    @PostMapping("/post")
    public String helloPost(){
        return "Hello world - POST";
    }

    @PutMapping("/put")
    public String helloPut(){
        return "Hello world - PUT";
    }

    @DeleteMapping("/delete")
    public String helloDelete(){
        return "Hello world - DELETE";
    }

    @PatchMapping("/patch")
    public String helloPatch(){
        return "Hello world - PATCH";
    }

    /*private boolean tokenVal(String token){
        String userId = jwtUtil.getKey(token); //obtener id del usuario
        return userId == null; //si el usuario no es nullo va bien
    }*/

//    @PostMapping("/saveUsers")
//    public UserModel saveUser(@RequestBody UserModel user){
//        return this.userService.saveUser(user);
//    }
//
//
//
//    @GetMapping(path = "/user/{id}")
//    public Optional<UserModel> getUserById(@PathVariable("id") Long id){
//        return this.userService.getUserById(id);
//    }
//
//    @GetMapping("/query")
//    public ArrayList<UserModel> getUsersByPriority(@RequestParam("priority") Integer priority){
//        return this.userService.getUserByPriority(priority);
//    }
//







    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") Long id) {
        boolean isDisabled = userService.disableUserById(id);
        if (isDisabled) {
            return ResponseEntity.ok("Usuario desactivado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }







}
