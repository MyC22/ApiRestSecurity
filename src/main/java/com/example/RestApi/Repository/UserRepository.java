package com.example.RestApi.Repository;

import com.example.RestApi.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByUsername(String username);
    boolean existsByEmail(String email);


    List<UserEntity> findByIsEnabledTrue();
    List<UserEntity> findByIsEnabledFalse();

}
