package com.gings.security.authentication;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import com.gings.security.JWTAuthentication;
import com.gings.security.JWTService;
import com.gings.security.UserAuthTokenInfo;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class WebsocketConnectAuthenticationFilter extends AbstractAuthenticationProcessingFilter{

    protected WebsocketConnectAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
                                                throws AuthenticationException, IOException, ServletException {
        
        String jwt = request.getHeader(JWTService.AUTHORIZATION);
        
        if(StringUtils.isEmpty(jwt)) {
            log.info("Received Empty jwt token.");
            
            throw new BadCredentialsException("JWT token is null");
        }
        
        UserAuthTokenInfo tokenInfo = new UserAuthTokenInfo(jwt);
        
        return getAuthenticationManager().authenticate(new JWTAuthentication(tokenInfo));
        
    }
    
    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
                                         FilterChain chain, Authentication authentication) 
                                                                 throws IOException, ServletException {
        
    }
    
}
