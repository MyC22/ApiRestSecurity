package com.example.RestApi.Utils;

import com.example.RestApi.model.entity.UserEntity;
import com.example.RestApi.repository.UserRepository;
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
