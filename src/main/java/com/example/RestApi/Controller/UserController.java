package com.example.RestApi.Controller;

//import com.example.RestApi.Model.UserModel;
//import com.example.RestApi.Services.UserService;
//import com.example.RestApi.Utils.JWTUtil;
import com.example.RestApi.Persistence.entity.UserEntity;
import com.example.RestApi.Services.UserDetailServiceImpl;
import com.example.RestApi.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

//@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/method")
public class UserController {

    @Autowired
    UserDetailServiceImpl userService;

    @Autowired
    JWTUtil jwtUtil;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEVELOPER')")
    public ArrayList<UserEntity> getUsers() {
        return userService.getUsers();
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
    Optional<UserEntity> userOptional = userService.getUserById(id);
    if (userOptional.isPresent()) {
        boolean isDeleted = userService.deleteUserById(id);
        if (isDeleted) {
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el usuario");
        }
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
    }
}


}
