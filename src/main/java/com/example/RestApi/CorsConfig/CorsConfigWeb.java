package com.example.RestApi.CorsConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfigWeb implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/users").allowedOrigins("http://127.0.0.1:5500");
        registry.addMapping("/api/delete/**").allowedOrigins("http://127.0.0.1:5500");
    }
}
