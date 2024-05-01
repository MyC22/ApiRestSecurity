package com.example.RestApi.Controller;

//import com.example.RestApi.Model.UserModel;
//import com.example.RestApi.Services.UserService;
//import com.example.RestApi.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/method")
public class UserController {

//    @Autowired
//    UserService userService;

    //@Autowired
    //JWTUtil jwtUtil;

//    @GetMapping("/users")
//    public ArrayList<UserModel> getUsers(/*@RequestHeader(value = "Authorization") String token*/){
//        /*if(tokenVal(token)){
//          return null;
//        }*/
//        return userService.getUsers();
//    }

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
//    @DeleteMapping(path = "/delete/{id}")
//    public void deleteUserById(/*@RequestHeader(value = "Authorization") String token ,*/ @PathVariable("id") Long id){
//       // if(tokenVal(token)){return;}
//        userService.deleteUserById(id);

   // }
}
