package com.gings.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class StompControllerExceptionHandler {
    
    private final WebSocketSessionManager webSocketSessionManager;
    
    public StompControllerExceptionHandler(WebSocketSessionManager webSocketSessionManager) {
        this.webSocketSessionManager = webSocketSessionManager;
    }
    
    @Around("execution(* com.gings.controller.ChatController.*(..))")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        
        if (log.isInfoEnabled()) {
            log.info("Starting stomp controller method {}#{}.", pjp.getTarget()
                                                              , pjp.getSignature().toShortString());
        }
        
        try {
            
            Object result = pjp.proceed();

            if(log.isInfoEnabled()){
                log.info("Method {}#{} finished successfully with returnning : {}", pjp.getTarget()
                                                                                  , pjp.getSignature()
                                                                                  , result);
            }
            
            return result;
            
        }catch(IllegalWebsocketAccessException e){
            
            String accessorId = String.valueOf(e.getAccessorId());
            
            log.error("Illegal websocket accessing detected. Illegal accessor : {}", accessorId);
            log.error("Occurred exception :{}", e);
            log.error("Now closing illegally accessed websocket connection.");
            
            webSocketSessionManager.close(accessorId);
            return null;
        }catch (Throwable e) {
            log.error("Error occurred,",  e);
            throw e;
        }
    }
}
