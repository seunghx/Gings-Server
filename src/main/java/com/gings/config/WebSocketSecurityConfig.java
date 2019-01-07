package com.gings.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * 
 * @author seunghyun
 *
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private static final String CONNECT_END_POINT = "/connect";
    
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .anyMessage()
                .authenticated();
    }
    
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
