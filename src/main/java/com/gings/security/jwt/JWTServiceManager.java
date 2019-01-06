package com.gings.security.jwt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author seunghyun
 *
 */
@Slf4j
public class JWTServiceManager {
    
    private final List<JWTService> jwtServices = new ArrayList<>();
    
    public JWTServiceManager(List<JWTService> jwtServices) {
        this.jwtServices.addAll(jwtServices);
    }
    
    /**
     * @param jwtService - 이 클래스에서 관리하는 JWTService 구현.
     */
    public void addJwtService(JWTService jwtService) {
        if(jwtService == null) {
            log.info("Received Null value parameter.");
            
            throw new NullPointerException("jwtService is null"); 
        }

        jwtServices.add(jwtService);
    }
    
    /**
     * 전달받은 인자 {@code token}을 처리 가능한 {@link JWTService} 구현을 반환.
     */
    public JWTService resolve(Class<? extends TokenInfo> token) {
        for(JWTService jwtService : jwtServices) {
            if(jwtService.support(token)) {
                return jwtService;
            }
        }
        
        log.warn("Unsupported Token class detected. Unsupported Token class : {}", token);
        
        throw new IllegalArgumentException("Unsupported Token class");
    }
}
