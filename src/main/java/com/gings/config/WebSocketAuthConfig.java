package com.gings.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.gings.security.authentication.StompConnectionInterceptor;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Configuration
public class WebSocketAuthConfig implements WebSocketMessageBrokerConfigurer{
    
    @Autowired
    private StompConnectionInterceptor connectInterceptor;
        
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
       registration.interceptors(connectInterceptor);
    }
}
