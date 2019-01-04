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

import lombok.extern.slf4j.Slf4j;

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
            
            throw new CredentialsExpiredException("");
        } catch(JWTVerificationException e) {
            
            String remote = getRequestAddr();
            
            log.error("Exception occurred while trying to authenticate user request.", e);
            log.warn("It might be illegal access!! Requesting remote user ip : {}", remote);
            
            throw new BadCredentialsException("");
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
