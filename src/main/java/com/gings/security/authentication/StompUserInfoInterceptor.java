package com.gings.security.authentication;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;



import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StompUserInfoInterceptor implements ChannelInterceptor{
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        log.info("pre send called");
        
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
                
        if(command == StompCommand.CONNECT) {
           log.debug("Stomp Connect message reached here. so just return this.");
           return message;
        }else {
            log.error("Request command : {} and user : {}", accessor.getCommand(), accessor.getUser());
            return message;
        }
        
    }
}
