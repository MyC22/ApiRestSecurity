package com.example.RestApi.Controller;

import com.example.RestApi.Model.UserModel;
import com.example.RestApi.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ArrayList<UserModel> getUsers(){
        return userService.getUsers();
    }

    @PostMapping
    public UserModel saveUser(@RequestBody UserModel usuario){
        return this.userService.saveUser(usuario);
    }

    @GetMapping(path = "/{id}")
    public Optional<UserModel> getUserById(@PathVariable("id") Long id){
        return this.userService.getUserById(id);
    }

    @GetMapping("/query")
    public ArrayList<UserModel> getUsersByPriority(@RequestParam("prioridad") Integer priority){
        return this.userService.getUserByPriority(priority);
    }

    @DeleteMapping(path = "/{id}")
    public String deleteUserById(@PathVariable("id") Long id){
        boolean ok = this.userService.deleteUserById(id);
        if (ok){
            return "Se elimino el usuario con el id " + id;
        }else{
            return "No se pudo eliminar el usuario con el id "+ id;
        }
    }
}
