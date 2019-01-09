package com.gings.utils;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionConnectListener implements ApplicationListener<SessionConnectEvent> {
    
    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        if(log.isInfoEnabled()) {

            log.info("New session request to connect : {}.", event.getMessage());
            
        }
    }
}
