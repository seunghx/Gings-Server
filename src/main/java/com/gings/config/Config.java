package com.gings.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import com.gings.controller.PrincipalArgumentResolver;
import com.gings.security.EmailAuthWTService;
import com.gings.security.DefaultJWTService;
import com.gings.security.JWTService;
import com.gings.security.JWTServiceManager;

import nz.net.ultraq.thymeleaf.LayoutDialect;


@Configuration
public class Config implements WebMvcConfigurer {
    
    @Autowired
    private SpringTemplateEngine templateEngine;
    
    @PostConstruct
    public void init() {
        templateEngine.addDialect(new LayoutDialect());
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public JWTServiceManager jwtServiceManager() {
        List<JWTService> jwtServices = new ArrayList<>();
        jwtServices.add(defaultJWTService());
        jwtServices.add(authNumberJWTService());
        
        return new JWTServiceManager(jwtServices);
    }
    
    @Bean
    public JWTService defaultJWTService() {
        return new DefaultJWTService();
    }
    
    @Bean
    public JWTService authNumberJWTService() {
        return new EmailAuthWTService();
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new PrincipalArgumentResolver());
    }
    
}
