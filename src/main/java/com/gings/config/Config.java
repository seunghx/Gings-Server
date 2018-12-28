package com.gings.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.catalina.Context;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gings.security.DefaultJWTService;
import com.gings.security.JWTService;
import com.gings.security.JWTServiceManager;

@Configuration
public class Config {
    
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
    
}
