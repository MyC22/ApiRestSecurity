package com.example.RestApi.Utils;

import com.example.RestApi.Persistence.Repository.UserRepository;
import com.example.RestApi.Persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public UserEntity getAuthenticatedUser() {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findUserEntityByUsername(authenticatedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));
    }
}
