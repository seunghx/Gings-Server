package com.gings.config;

import java.util.ArrayList;
import java.util.List;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gings.controller.PrincipalArgumentResolver;
import com.gings.security.DefaultJWTService;
import com.gings.security.JWTService;
import com.gings.security.JWTServiceManager;


@Configuration
public class Config implements WebMvcConfigurer {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public JWTServiceManager jwtServiceManager() {
        List<JWTService> jwtServices = new ArrayList<>();
        jwtServices.add(defaultJWTService());
        
        return new JWTServiceManager(jwtServices);
    }
    
    @Bean
    public DefaultJWTService defaultJWTService() {
        return new DefaultJWTService();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new PrincipalArgumentResolver());
    }
    
}
