package com.gings.security.authentication;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.gings.security.jwt.UserAuthTokenInfo;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StompUserInfoInterceptor implements ChannelInterceptor{
    
    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        
        if(command == StompCommand.CONNECTED) {
            log.error("Header check for connected frame.");
            
            log.error("SimpUser header : {}", accessor.getUser());
        }
        
        return message;
    }
    
}
