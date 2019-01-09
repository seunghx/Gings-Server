package com.gings.security;

import java.security.Principal;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author seunghyun
 *
 */
/**
 * 
 * Websocket connection 요청 인증을 위한 {@link Authentication} 구현.
 * 
 * 이 클래스는 인증 완료됨을 나타내기위해 정의하였기 때문에 이 클래스의 {@link #isAuthenticated()}는
 * 항상 true를 반환.
 * 
 * @author seunghyun
 *
 */
@Slf4j
public class StompConnectingAuthentication implements Authentication{
    
    private static final long serialVersionUID = 2561946285897548899L;
    
    private final WebSocketPrincipal principal;
   
    public StompConnectingAuthentication(WebSocketPrincipal principal) {
        
        validate(principal);
        
        this.principal = principal;
    }
    
    private void validate(WebSocketPrincipal principal) {
        if(principal == null || principal.getRole() == null || 
           StringUtils.isEmpty(principal.getEmail()) || principal.getUserId() == 0) {
            
            log.info("Invalid principal detected. Invalid principal : {}", principal);
            
            throw new IllegalArgumentException("Invalid principal argument");
        }
    }
    
    @Override
    public String getName() {
        return principal.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return principal.getRole().getAuthorities();
    }

    /**
     * 이 클래스의 {@link Authentication#getCredentials()} 구현 메서드는 사용되지 않을 것.
     * 
     * @throws UnsupportedOperationException 지원 않는 메서드 호출로 반환 값이 비일관적인 상황이 될 바에
     *                                       예외를 던져 개발자의 실수 방지.
     */
    @Override
    public Object getCredentials() {
        
        log.debug("Illegal method call detected.");
        
        throw new UnsupportedOperationException();
    }
    
    /**
     * 
     * @throws UnsupportedOperationException 지원 않는 메서드 호출로 반환 값이 비일관적인 상황이 될 바에
     *                                       예외를 던져 개발자의 실수 방지.
     */
    @Override
    public Object getDetails() {

        log.debug("Illegal method call detected.");
        
        throw new UnsupportedOperationException();
    }

    @Override
    public WebSocketPrincipal getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if(!isAuthenticated) {
            log.debug("Illegal method call detected.");
        }
        
        return;
    }

}
