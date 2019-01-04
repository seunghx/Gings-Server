package com.gings.security.authentication;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.gings.dao.UserMapper;
import com.gings.security.JWTService;
import com.gings.security.JWTServiceManager;
import com.gings.security.Principal;
import com.gings.security.UserAuthTokenInfo;
import com.gings.security.WebsocketConnectingAuthentication;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class WebsocketConnectAuthenticationFilter extends AbstractAuthenticationProcessingFilter{


    private static final String XFF_HEADER_NAME = "X-Forwarded-For";
    
    private final JWTServiceManager jwtServiceManager;
    private final UserMapper userMapper;
    private final ModelMapper modelMapper = new ModelMapper();
    
    protected WebsocketConnectAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher,
                                                   JWTServiceManager jwtServiceManager,
                                                   UserMapper userMapper) {
        super(requiresAuthenticationRequestMatcher);
        this.jwtServiceManager = jwtServiceManager;
        this.userMapper = userMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
                                                throws AuthenticationException, IOException, ServletException {
        
        String jwt = request.getHeader(JWTService.AUTHORIZATION);
        
        if(StringUtils.isEmpty(jwt)) {
            log.info("Received Empty jwt token.");
            
            throw new BadCredentialsException("JWT token is null");
        }
        // Bearer 검사
        
   
        try {
            UserAuthTokenInfo tokenInfo = authenticateInternal(jwt);

            Principal principal = modelMapper.map(userMapper.findByUserId(tokenInfo.getUid()), 
                                                      Principal.class);
                
            return new WebsocketConnectingAuthentication(principal);
        }catch(Exception e){
            throw e;
        }
      
    }

    private UserAuthTokenInfo authenticateInternal(String jwt) {
        
        UserAuthTokenInfo tokenInfo = new UserAuthTokenInfo(jwt);
        
        return (UserAuthTokenInfo)jwtServiceManager.resolve(tokenInfo.getClass())
                                                   .decode(tokenInfo);
    }
    

    private String getRequestAddr() {
        HttpServletRequest request = ((ServletRequestAttributes)
                                                    RequestContextHolder.getRequestAttributes())
                                                                        .getRequest();
        
        String remote = request.getHeader(XFF_HEADER_NAME);
        
        return remote != null? remote : request.getRemoteAddr();
    }
    
    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
                                         FilterChain chain, Authentication authentication) 
                                                                 throws IOException, ServletException {
        
        

        SecurityContext sc = SecurityContextHolder.createEmptyContext();
        sc.setAuthentication(authentication);
        SecurityContextHolder.setContext(sc);
        
        chain.doFilter(request, response);
    }
    
}
