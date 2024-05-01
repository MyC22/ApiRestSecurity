package com.example.RestApi.Persistence.entity.Repository;

import com.example.RestApi.Persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByUsername(String username);

//    @Query("SELECT u FROM UserEntity u WHERE u.username = ?")
//    Optional<UserEntity> findUsers(String username);

//    @Query("SELECT u FROM UserModel u WHERE u.email = :email AND u.password = :password")
//    public List<UserModel> getUserByCredentials(@Param("email") String email, @Param("password") String password);

}
