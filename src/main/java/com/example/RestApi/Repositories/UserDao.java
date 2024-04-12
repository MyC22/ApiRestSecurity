package com.example.RestApi.Repositories;

import com.example.RestApi.Model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface UserDao extends JpaRepository<UserModel, Long> {

    public abstract ArrayList<UserModel> findByPriority(Integer priority);

    @Query("SELECT u FROM UserModel u WHERE u.email = :email AND u.password = :password")
    public List<UserModel> getUserByCredentials(@Param("email") String email, @Param("password") String password);

}
