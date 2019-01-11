package com.gings.security.authentication;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TestInterCeptor implements ChannelInterceptor{
    public  Message<?>  preSend(Message<?> message, MessageChannel channel){
        
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        
        log.error("Presend to out bound : {}",message);
        log.error("Outbound command : {}", accessor.getCommand());
        
        return message;
    }
}
