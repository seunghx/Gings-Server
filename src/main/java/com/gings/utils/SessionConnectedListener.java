package com.gings.utils;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionConnectedListener implements ApplicationListener<SessionConnectedEvent> {

    // Methods
    // ==========================================================================================================================

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        if(log.isInfoEnabled()) {

            log.info("New session connected : {}.", event.getMessage());
            
        }
    }

}

