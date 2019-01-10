package com.gings.utils;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class StompSessionDisconnectListener implements ApplicationListener<SessionDisconnectEvent>{

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        if(log.isInfoEnabled()) {
            log.info("Session Disconnect : {}.", event.getMessage());
        }        
    }
    
}
