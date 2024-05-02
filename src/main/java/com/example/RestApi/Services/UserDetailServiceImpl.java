package com.example.RestApi.Services;

import com.example.RestApi.Controller.dto.AuthCreateUserRequest;
import com.example.RestApi.Controller.dto.AuthLoginRequest;
import com.example.RestApi.Controller.dto.AuthResponse;
import com.example.RestApi.Persistence.entity.Repository.RoleRepository;
import com.example.RestApi.Persistence.entity.Repository.UserRepository;
import com.example.RestApi.Persistence.entity.RoleEntity;
import com.example.RestApi.Persistence.entity.UserEntity;
import com.example.RestApi.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

//    public ArrayList<UserModel> getUsers(){
//        return (ArrayList<UserModel>) userDao.findAll();
//    }
//
//    public UserModel saveUser(UserModel user){
//        return userDao.save(user);
//    }
//
//    public Optional<UserModel> getUserById(Long id){
//        return userDao.findById(id);
//    }
//
//    public ArrayList<UserModel> getUserByPriority(Integer priority){
//        return userDao.findByPriority(priority);
//    }
//
//    public UserModel registerUser(UserModel user){
//        return userDao.save(user);
//    }
//
//    public boolean deleteUserById(Long id){
//        try {
//            userDao.deleteById(id);
//            return true;
//        }catch (Exception err){
//            return false;
//        }
//    }


    //find user in database
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserEntityByUsername((username))
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username +" no existe"));
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userEntity.getRoles()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(userEntity.getUsername()
        ,userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList);
    }

    //Login user
    public AuthResponse loginUser(AuthLoginRequest authLoginRequest){
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.createToken(authentication);

        return new AuthResponse(username, "User Logged succesfully", accessToken, true);
    }

    //authentication method
    public Authentication authenticate(String username, String password){
        UserDetails userDetails = this.loadUserByUsername(username);

        if(userDetails == null){
            throw new BadCredentialsException("Invalid username or password");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid Password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public AuthResponse createUser(AuthCreateUserRequest authCreateUserRequest){
        String username = authCreateUserRequest.username();
        String password = authCreateUserRequest.password();
        List<String> roleRequest = authCreateUserRequest.roleRequest().roleListName();

        //find roles that match the roles I am submitting in roleRequest
        Set<RoleEntity> roleEntitySet = roleRepository.findRoleEntitiesByRoleEnumIn(roleRequest).stream().collect(Collectors.toSet());

        if (roleEntitySet.isEmpty()){
            throw new IllegalArgumentException("The roles specified does not exist");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roleEntitySet)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .isEnabled(true)
                .build();

        //save user in database
        UserEntity userCreated = userRepository.save(userEntity);

        //permission list
        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        //GET clients of user and add ROLE_ and creating like a SimpleGrantedAuthority for spring security work with this like a rol
        userCreated.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        //GET permission of user
        userCreated.getRoles()
                .stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        //Authenticate user For Spring security
        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(), authorityList);

        //Token created with authentication
        String accessToken = jwtUtil.createToken(authentication);

        //Create the AuthResponse, it's in dto
        AuthResponse authResponse = new AuthResponse(userCreated.getUsername(), "User Created Successfully", accessToken, true);
        return authResponse;
    }
}
