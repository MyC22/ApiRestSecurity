package com.example.RestApi.Services;

import com.example.RestApi.Model.UserModel;
import com.example.RestApi.Repositories.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public ArrayList<UserModel> getUsers(){
        return (ArrayList<UserModel>) userDao.findAll();
    }

    public UserModel saveUser(UserModel user){
        return userDao.save(user);
    }

    public Optional<UserModel> getUserById(Long id){
        return userDao.findById(id);
    }

    public ArrayList<UserModel> getUserByPriority(Integer priority){
        return userDao.findByPriority(priority);
    }

    public UserModel verifyUser(String email, String password){
        return userDao.verifyUser(email, password);
    }

    public boolean deleteUserById(Long id){
        try {
            userDao.deleteById(id);
            return true;
        }catch (Exception err){
            return false;
        }
    }
}
