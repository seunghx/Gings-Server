package com.gings.security.authentication;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.gings.model.ApiError;
import com.gings.security.JWTService;
import com.gings.security.JWTServiceManager;
import com.gings.security.UserAuthTokenInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Aspect
public class AuthAspect {

    private static final String AUTHORIZATION = "Authorization";
    private static final String UNAUTHORIZED_MSG = "response.authentication.failure";

    private static final String BEARER_SCHME = "Bearer ";
    
    /**
     * 실패 시 기본 반환 Response
     */
    private static ResponseEntity<ApiError> FAILURE_RES;

    private final HttpServletRequest httpServletRequest;
    private final JWTServiceManager jwtServiceManager;
    private final MessageSource msgSource;

    /**
     * Repository 의존성 주입
     */
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
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED.value()
                                       , msgSource.getMessage(UNAUTHORIZED_MSG, null, Locale.getDefault()));
        
        FAILURE_RES = new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }
    
    /*
    @Around("@annotation(org.sopt.seminar7.utils.auth.Auth)")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        final String jwt = httpServletRequest.getHeader(AUTHORIZATION);
        
        if (jwt == null) return FAILURE_RES;
        
        JWTService jwtService = jwtServiceManager.resolve(UserAuthTokenInfo.class);
        
        jwtService.decode(new UserAuthTokenInfo(jwt));
        
        if (token == null) {
            return RES_RESPONSE_ENTITY;
        } else {
            final User user = userMapper.findByUserIdx(token.getUser_idx());
            //유효 사용자 검사
            if (user == null) return RES_RESPONSE_ENTITY;
            return pjp.proceed(pjp.getArgs());
        }
    }*/
}

