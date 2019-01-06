package com.gings.security.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gings.dao.UserMapper;
import com.gings.security.StompConnectingAuthentication;
import com.gings.security.WebSocketPrincipal;
import com.gings.security.jwt.JWTService;
import com.gings.security.jwt.JWTServiceManager;
import com.gings.security.jwt.UserAuthTokenInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * spring security websocket suuport를 이용할 경우 websocket connect 요청을 한 유저 정보를 이용해
 * connection이 유지되는 동안의 유저 정보를 가져올 수 있음. 이를 위해 connect 요청에 대한 인증 정보를 
 * spring security가 알 수 있도록 아래 필터 정의. 
 * 
 * (명시적으로 stomp header에 요청 유저의 id를 전달하는 것은 악의적으로 바꿀 수 있으므로 좋지 않음.)
 * 
 * @author seunghyun
 *
 */
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
        
        if(!isValidToken(jwt)) {
            throw new BadCredentialsException("Invalid JWT token.");
        }
        
        try {
            
            UserAuthTokenInfo tokenInfo = authenticateInternal(jwt);

            WebSocketPrincipal principal = modelMapper.map(userMapper.findByUserId(tokenInfo.getUid()), 
                                                           WebSocketPrincipal.class);
            
            return new StompConnectingAuthentication(principal);
            
        }catch(TokenExpiredException e){
            log.info("Request user token expired.");
            
            throw new CredentialsExpiredException("JWT token expired.", e);
        } catch(JWTVerificationException e) {
            String remote = getRequestAddr();
            
            log.warn("Invalid jwt token detected.", e);
            log.warn("It might be illegal access!! Requesting remote user ip : {}", remote);
            
            throw new BadCredentialsException("Invalid JWT token.", e);
        }
    }
    
    private boolean isValidToken(String jwt) {

        if(StringUtils.isEmpty(jwt)) {
            log.warn("Received Empty jwt token.");
            
            return false;
        }
        
        if(!jwt.startsWith(JWTService.BEARER_SCHEME)) {
            log.warn("Invalid jwt token. token does not starts with Bearer");
            
            return false;
        }
        
        return true;
    }

    private UserAuthTokenInfo authenticateInternal(String jwt) {
        
        UserAuthTokenInfo tokenInfo = new UserAuthTokenInfo(jwt);
        
        return (UserAuthTokenInfo)jwtServiceManager.resolve(tokenInfo.getClass())
                                                   .decode(tokenInfo);
    }

    private String getRequestAddr() {
        HttpServletRequest request = 
                    ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
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
