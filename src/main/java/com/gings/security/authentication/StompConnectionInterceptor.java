package com.gings.security.authentication;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;

import com.gings.security.jwt.JWTService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StompConnectionInterceptor implements ChannelInterceptor {
    
    /**
     * stage 관련 unsubscription 메세지가 전달될 경우 이를 오직 {@link SimpleBrokerMessageHandler}에만
     * 전달한다.<br>
     * 
     * 또한 메서드의 반환 타입을 고려하여 임의로 만든 길이 0의 메세지를 생성하여 특정 엔드포인트({@link MessageMapping)
     * 로 전달하게 하였다.이 엔드 포인트로의 메세지를 처리하는 핸들러 메서드는 전달 받은 메세지를 어디에도 전달하지 않기 때문에 
     * 결국 메세지는 무시된다.
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
                
        if(command == StompCommand.CONNECTED) {
            String jwt = accessor.getFirstNativeHeader(JWTService.AUTHORIZATION);
            log.error("Received jwt : {}", jwt);
        }
        
       return message;

    }
}
