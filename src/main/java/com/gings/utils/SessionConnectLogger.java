package com.gings.utils;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 커넥트 요청 event 발생시 logging 수행
 * 
 * @author seunghyun
 *
 */
@Slf4j
@Component
public class SessionConnectLogger implements ApplicationListener<SessionConnectEvent> {
    
    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        if(log.isInfoEnabled()) {

            log.info("New session request to connect : {}.", event.getMessage());
            
        }
    }
}
