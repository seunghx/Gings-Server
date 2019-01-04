package com.gings.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gings.security.DefaultJWTService;
import com.gings.security.EmailAuthWTService;
import com.gings.security.JWTService;
import com.gings.security.JWTServiceManager;

/**
 * 
 * @author seunghyun
 *
 */
@Configuration
//@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String WS_CONNECT = "/connect";
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                .anyRequest()
                .permitAll()
                .antMatchers(WS_CONNECT)
                .authenticated()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

    @Bean
    public JWTServiceManager jwtServiceManager() {
        List<JWTService> jwtServices = new ArrayList<>();
        jwtServices.add(defaultJWTService());
        jwtServices.add(emailNumberJWTService());
        
        return new JWTServiceManager(jwtServices);
    }
    
    @Bean
    public JWTService defaultJWTService() {
        return new DefaultJWTService();
    }
    
    @Bean
    public JWTService emailNumberJWTService() {
        return new EmailAuthWTService();
    }
}