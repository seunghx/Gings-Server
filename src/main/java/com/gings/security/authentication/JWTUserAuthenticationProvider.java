package com.gings.security.authentication;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gings.security.JWTAuthentication;
import com.gings.security.JWTServiceManager;
import com.gings.security.UserAuthTokenInfo;
import com.gings.security.authentication.AuthAspect;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 현재 {@link AuthAspect}로 jwt 기반 유저 인증 진행 중이나, 
 * 
 * websocket 통신 중 user 정보를 편하게 가져오기 위해 spring security를 이용할 필요가 있어서 
 * websocket connect 요청에 대한 유저 인증을 아래 클래스가 담당.
 * 
 * (spring security websocket support는 connect 요청 시의 유저의 {@link Authentication}을 재사용)
 * 
 * 
 * @author seunghyun
 *
 */
@Slf4j
public class JWTUserAuthenticationProvider implements AuthenticationProvider {
    
    
    private static final String XFF_HEADER_NAME = "X-Forwarded-For";
    
    private final JWTServiceManager jwtServiceManager;
    
    public JWTUserAuthenticationProvider(JWTServiceManager jwtServiceManager) {
        this.jwtServiceManager = jwtServiceManager;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        if(!(authentication instanceof JWTAuthentication)){
            log.info("Authentication type parameter {} is not accepted for this class.");
            throw new InternalAuthenticationServiceException("Received unsupported authentication.");
        }
        
        try {
            UserAuthTokenInfo tokenInfo = authenticateInternal((JWTAuthentication)authentication);

            return new JWTAuthentication(tokenInfo);

        }catch(TokenExpiredException e){
            log.info("Request user token expired.");
            
            throw new CredentialsExpiredException("JWT token expired");
        } catch(JWTVerificationException e) {
            
            String remote = getRequestAddr();
            
            log.error("Exception occurred while trying to authenticate user request.", e);
            log.warn("It might be illegal access!! Requesting remote user ip : {}", remote);
            
            throw new BadCredentialsException("Invalid JWT token");
        }
    }
    
    private UserAuthTokenInfo authenticateInternal(JWTAuthentication authentication) {
        UserAuthTokenInfo tokenInfo = 
                Optional.ofNullable(authentication.getCredentials())
                        .<InternalAuthenticationServiceException>orElseThrow(() -> {
                            log.info("Received null valued authentication.");
                                                  
                            throw new InternalAuthenticationServiceException("Null value authentication");
                        });
        
        
        return (UserAuthTokenInfo)jwtServiceManager.resolve(tokenInfo.getClass())
                                                   .decode(tokenInfo);
    }

    @Override
    public boolean supports(Class<?> authentication) {
       if(authentication == null) {
           log.info("Received null value authentication.");
           
           throw new InternalAuthenticationServiceException("Null value authentication class.");
       }
        
        return JWTAuthentication.class.isAssignableFrom(authentication);
    }

    private String getRequestAddr() {
        HttpServletRequest request = ((ServletRequestAttributes)
                                                    RequestContextHolder.getRequestAttributes())
                                                                        .getRequest();
        
        String remote = request.getHeader(XFF_HEADER_NAME);
        
        return remote != null? remote : request.getRemoteAddr();
    }
}
