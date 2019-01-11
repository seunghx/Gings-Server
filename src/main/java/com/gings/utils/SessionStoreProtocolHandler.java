package com.gings.utils;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionStoreProtocolHandler extends SubProtocolWebSocketHandler {

    private final WebSocketSessionManager sessionManager;
    
    public SessionStoreProtocolHandler(MessageChannel clientInboundChannel, 
                                       SubscribableChannel clientOutboundChannel,
                                       WebSocketSessionManager sessionManager) {
        super(clientInboundChannel, clientOutboundChannel);
        this.sessionManager = sessionManager;
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connection established with session : {}", session);
        
        sessionManager.onConnect(session);
        
    } 
}
