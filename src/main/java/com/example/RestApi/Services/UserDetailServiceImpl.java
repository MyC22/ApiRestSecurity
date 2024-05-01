package com.example.RestApi.Services;

import com.example.RestApi.Controller.dto.AuthLoginRequest;
import com.example.RestApi.Controller.dto.AuthResponse;
import com.example.RestApi.Persistence.entity.Repository.UserRepository;
import com.example.RestApi.Persistence.entity.UserEntity;
import com.example.RestApi.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    UserRepository userRepository;

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


    public AuthResponse loginUser(AuthLoginRequest authLoginRequest){
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.createToken(authentication);

        return new AuthResponse(username, "User Logged succesfully", accessToken, true);
    }

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

}
