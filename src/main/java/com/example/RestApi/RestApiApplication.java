package com.example.RestApi;

import com.example.RestApi.Persistence.Repository.UserRepository;
import com.example.RestApi.Persistence.entity.PermissionEntity;
import com.example.RestApi.Persistence.entity.RoleEntity;
import com.example.RestApi.Persistence.entity.RoleEnum;
import com.example.RestApi.Persistence.entity.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.Set;

@EnableScheduling
@SpringBootApplication
public class RestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApiApplication.class, args);
	}

//	@Bean
//	CommandLineRunner init(UserRepository userRepository){
//		return args -> {
//			/* CREATE PERMISSIONS */
//			PermissionEntity createPermission = PermissionEntity.builder()
//					.name("CREATE")
//					.build();
//
//			PermissionEntity readPermission = PermissionEntity.builder()
//					.name("READ")
//					.build();
//
//			PermissionEntity uptadePermission = PermissionEntity.builder()
//					.name("UPDATE")
//					.build();
//
//			PermissionEntity deletePermission = PermissionEntity.builder()
//					.name("DELETE")
//					.build();
//
//			PermissionEntity refactorPermission = PermissionEntity.builder()
//					.name("REFACTOR")
//					.build();
//
//			/*CREATE ROLES*/
//
//			RoleEntity roleAdmin = RoleEntity.builder()
//					.roleEnum(RoleEnum.ADMIN)
//					.permissionList(Set.of(createPermission, readPermission, uptadePermission, deletePermission))
//					.build();
//
//			RoleEntity roleUser = RoleEntity.builder()
//					.roleEnum(RoleEnum.USER)
//					.permissionList(Set.of(createPermission, readPermission))
//					.build();
//
//			RoleEntity roleInvited = RoleEntity.builder()
//					.roleEnum(RoleEnum.INVITED)
//					.permissionList(Set.of(readPermission))
//					.build();
//
//			RoleEntity roleDeveloper = RoleEntity.builder()
//					.roleEnum(RoleEnum.DEVELOPER)
//					.permissionList(Set.of(createPermission, readPermission, uptadePermission, deletePermission, refactorPermission))
//					.build();
//
//
//			/*CREATE USERS*/
//
//			UserEntity userDenis = UserEntity.builder()
//					.username("denis")
//					.password("$2a$10$nx4rLgXaYjiXLf6lxjoa7uQSlcKnN4H959Tx9p8j2X2Q3WAPqP2sO")
//					.isEnabled(true)
//					.accountNoExpired(true)
//					.accountNoLocked(true)
//					.credentialNoExpired(true)
//					.roles(Set.of(roleAdmin))
//					.build();
//
//			UserEntity userDaniel = UserEntity.builder()
//					.username("daniel")
//					.password("$2a$10$nx4rLgXaYjiXLf6lxjoa7uQSlcKnN4H959Tx9p8j2X2Q3WAPqP2sO")
//					.isEnabled(true)
//					.accountNoExpired(true)
//					.accountNoLocked(true)
//					.credentialNoExpired(true)
//					.roles(Set.of(roleUser))
//					.build();
//
//			UserEntity userAndrea = UserEntity.builder()
//					.username("andrea")
//					.password("$2a$10$nx4rLgXaYjiXLf6lxjoa7uQSlcKnN4H959Tx9p8j2X2Q3WAPqP2sO")
//					.isEnabled(true)
//					.accountNoExpired(true)
//					.accountNoLocked(true)
//					.credentialNoExpired(true)
//					.roles(Set.of(roleInvited))
//					.build();
//
//			UserEntity userAny = UserEntity.builder()
//					.username("any")
//					.password("$2a$10$nx4rLgXaYjiXLf6lxjoa7uQSlcKnN4H959Tx9p8j2X2Q3WAPqP2sO")
//					.isEnabled(true)
//					.accountNoExpired(true)
//					.accountNoLocked(true)
//					.credentialNoExpired(true)
//					.roles(Set.of(roleDeveloper))
//					.build();
//
//			userRepository.saveAll(List.of(userDenis, userDaniel, userAndrea, userAny));
//		};
//	}
}
