package com.example.RestApi.Persistence.Repository;

import com.example.RestApi.Persistence.entity.RoleEntity;
import com.example.RestApi.Persistence.entity.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    List<RoleEntity> findRoleEntitiesByRoleNameIn(List<RoleEnum> roleNames); // 🔹 Ahora usa RoleEnum

    Optional<RoleEntity> findByRoleName(RoleEnum roleName); // 🔹 Ahora usa RoleEnum
}
