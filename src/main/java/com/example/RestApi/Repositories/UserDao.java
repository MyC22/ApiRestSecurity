package com.example.RestApi.Repositories;

import com.example.RestApi.Model.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface UserDao extends CrudRepository<UserModel, Long> {

    public abstract ArrayList<UserModel> findByPriority(Integer priority);



}
