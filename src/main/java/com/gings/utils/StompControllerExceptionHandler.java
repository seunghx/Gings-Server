package com.gings.utils;

import static com.gings.security.jwt.JWTService.AUTHORIZATION;
import static com.gings.security.jwt.JWTService.BEARER_SCHEME;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gings.security.Principal;
import com.gings.security.jwt.JWTService;
import com.gings.security.jwt.UserAuthTokenInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class StompControllerExceptionHandler {
    
    @Around("execution(* com.gings.controller.PushNotificationController.*(..))")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        
        if (log.isInfoEnabled()) {
            log.info("Starting controller method {}#{}.", pjp.getTarget()
                                                        , pjp.getSignature().toShortString());
        }
        try {
            Object result = pjp.proceed();

            if (log.isInfoEnabled()) {
                log.info("Method {}#{} finished successfully returnning : {}.", pjp.getTarget()
                                                                              , pjp.getSignature()
                                                                              , result);
            }
            return result;
        } catch (Exception e) {
            log.error("Exception occurred,",  e);
        }
        
        return null;
    }
}
