package com.example.RestApi.repository;

import com.example.RestApi.enums.RoleEnum;
import com.example.RestApi.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    List<RoleEntity> findRoleEntitiesByRoleNameIn(List<RoleEnum> roleNames); // 🔹 Ahora usa RoleEnum

    Optional<RoleEntity> findByRoleName(RoleEnum roleName); // 🔹 Ahora usa RoleEnum
}
