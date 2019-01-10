package com.gings.controller;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.gings.utils.IllegalWebsocketAccessException;
import com.gings.utils.StompControllerExceptionHandler;
import com.gings.utils.WebSocketSessionManager;

import lombok.extern.slf4j.Slf4j;

@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
@Aspect
@Component
public class ControllerLoggingAspect {
    
    @Around("execution(* com.gings.controller.*.*(..))")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {

        if (log.isInfoEnabled()) {
            log.info("Starting controller method {}#{}.", pjp.getTarget()
                    , pjp.getSignature().toShortString());
        }

        try {
            Object result = pjp.proceed();

            if(log.isInfoEnabled()){
                log.info("Method {}#{} finished successfully", pjp.getTarget(), 
                                                               pjp.getSignature().toShortString());
            }

            return result;

        }catch(Exception e){
            log.error("Error occurred,",  e);
            throw e;
        }catch (Throwable e) {
            log.error("Error occurred,",  e);
            throw e;
        }
    }
}
