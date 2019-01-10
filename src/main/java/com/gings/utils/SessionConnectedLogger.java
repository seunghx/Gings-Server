package com.gings.utils;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionConnectedLogger implements ApplicationListener<SessionConnectedEvent>{
   
    
    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        if(log.isInfoEnabled()) {

            log.info("New session connected : {}.", event.getMessage());
            log.info("Newly connected user : {}", event.getUser());
        }
    }

}

