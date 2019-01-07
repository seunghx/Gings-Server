package com.gings.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.gings.dao.UserMapper;
import com.gings.security.authentication.StompConnectAuthenticationFilter;
import com.gings.security.jwt.DefaultJWTService;
import com.gings.security.jwt.EmailAuthWTService;
import com.gings.security.jwt.JWTService;
import com.gings.security.jwt.JWTServiceManager;

/**
 * 
 * @author seunghyun
 *
 */
@Configuration
//@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static final String STOMP_CONNECT = "/connect";
    
    @Autowired
    private UserMapper userMapper;
   
    
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
           //     .antMatchers(WS_CONNECT)
           //     .authenticated()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(stompConnectAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }
    
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {

        return super.authenticationManagerBean();
    }
    
    @Bean
    public StompConnectAuthenticationFilter stompConnectAuthenticationFilter() throws Exception {
        StompConnectAuthenticationFilter filter = 
                                 new StompConnectAuthenticationFilter(connectRequestMatcher(), 
                                                                      jwtServiceManager(), 
                                                                      userMapper);
        
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
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
    
    private RequestMatcher connectRequestMatcher() {
        RequestMatcher reqMatcher = new AntPathRequestMatcher(STOMP_CONNECT, HttpMethod.POST.toString());
        return reqMatcher;
    }
}