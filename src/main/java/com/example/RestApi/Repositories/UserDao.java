package com.example.RestApi.Repositories;

import com.example.RestApi.Model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserDao extends JpaRepository<UserModel, Long> {

    public abstract ArrayList<UserModel> findByPriority(Integer priority);

    @Query("SELECT u FROM UserModel u WHERE u.email = :email AND u.password = :password")
    public UserModel verifyUser(String email, String password);

}
