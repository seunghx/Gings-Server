package com.gings.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.gings.security.JWTAuthentication;
import com.gings.security.JWTServiceManager;
import com.gings.security.TokenInfo;
import com.gings.security.UserAuthTokenInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTUserAuthenticationProvider implements AuthenticationProvider {
    
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
        
        JWTAuthentication jwtAuthentication = (JWTAuthentication)authentication;
        
        
        return null;
    }
    
    private TokenInfo authenticateInternal(UserAuthTokenInfo tokenInfo) {
        return jwtServiceManager.resolve(tokenInfo.getClass())
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

}
