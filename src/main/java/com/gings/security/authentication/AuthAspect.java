package com.gings.security.authentication;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.gings.controller.Principal;
import com.gings.model.ApiError;
import com.gings.security.JWTService;
import com.gings.security.JWTServiceManager;
import com.gings.security.TokenInfo;
import com.gings.security.UserAuthTokenInfo;

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
public class AuthAspect {
 
    public static final String PRINCIPAL = "Princial";
    
    private static final String UNAUTHORIZED_MSG = "response.authentication.failure";

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_SCHEME = "Bearer ";
    
    private static final Class<? extends UserAuthTokenInfo> USING_TOKEN_INFO  = UserAuthTokenInfo.class;
    
    /**
     * 인증 실패 시 기본 반환 Response
     */
    private static ResponseEntity<ApiError> AUTH_FAILURE_RES;

    private final HttpServletRequest httpServletRequest;
    private final JWTServiceManager jwtServiceManager;
    private final MessageSource msgSource;

    public AuthAspect(HttpServletRequest httpServletRequest, JWTServiceManager jwtServiceManager,
                      MessageSource msgSource) {
        
        this.httpServletRequest = httpServletRequest;
        this.jwtServiceManager = jwtServiceManager;
        this.msgSource = msgSource;
        
    }
    
    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }
    
    @PostConstruct
    public void init() {
        ApiError authFailRES = new ApiError(HttpStatus.UNAUTHORIZED.value(),
                                               msgSource.getMessage(UNAUTHORIZED_MSG, null, 
                                                                    Locale.getDefault()));
        
        AUTH_FAILURE_RES = new ResponseEntity<>(authFailRES, HttpStatus.UNAUTHORIZED);
    }
    
    
    @Around("@annotation(com.gings.security.authentication.Authentication)")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        
        log.info("Starting to authenticate user info.");
        
        String jwt = httpServletRequest.getHeader(AUTHORIZATION);
        
        if (jwt == null) {
            log.info("JWT token from request header does not exist.");
            
            return AUTH_FAILURE_RES;
        }
        
        jwt = jwt.replace(BEARER_SCHEME, "");
        JWTService jwtService = jwtServiceManager.resolve(USING_TOKEN_INFO);
            
         try {

            UserAuthTokenInfo token = (UserAuthTokenInfo)jwtService.decode(new UserAuthTokenInfo(jwt));
            httpServletRequest.setAttribute(PRINCIPAL, new Principal(token.getUid(), token.getUserRole()));
            
        }catch(JWTCreationException e) {
            log.error("Exception occurred while trying to authenticate user request.", e);
            
            return AUTH_FAILURE_RES;
        }
         
        return pjp.proceed(pjp.getArgs());
    }
}

