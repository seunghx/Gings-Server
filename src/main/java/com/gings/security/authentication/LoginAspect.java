package com.gings.security.authentication;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.gings.dao.UserMapper;
import com.gings.security.JWTServiceManager;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author seunghyun
 *
 */
@Slf4j
@Component
@Order(1)
@Aspect
public class LoginAspect {
    
    private final HttpServletRequest request;
    private final UserMapper userMapper;
    private final JWTServiceManager jwtServiceManager;
    
    public LoginAspect(HttpServletRequest request, UserMapper userMapper, JWTServiceManager jwtServiceManager) {
        
        this.request = request;
        this.userMapper = userMapper;
        this.jwtServiceManager = jwtServiceManager;
    }
    
    
}
