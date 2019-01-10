package com.gings.utils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * spring controller 또는 service layer에서 잘못된 요청을 시도한 session과의 연결을 강제적으로
 * disconnect할 방법이 없어 정의하게 된 클래스이다.
 * 
 * @author seunghyun
 *
 */
@Slf4j
@Component
public class WebSocketSessionManagerImpl implements WebSocketSessionManager  {
    
    private final Map<String, WebSocketSession> socketSessionMap = new ConcurrentHashMap<>();
    
    public void onConnect(WebSocketSession session) {
        validateOrFail(session);
        
        log.info("Newly connected session validated. Now register session info.");
        
        String user = session.getPrincipal().getName();
        
        socketSessionMap.put(user, session);
        
        log.info("Newly registered session : {} with user : {}", session, user);
    }
    
    public void onDisconnect(String userId) {
        log.info("Starting to remove session info for user {}", userId);
        
        socketSessionMap.remove(userId);
        
        log.info("Disconnect finished successfullly.");
    }
    
    public void close(String userId) {
        log.info("Starting to close session forcely for user {}", userId);
        WebSocketSession session = socketSessionMap.get(userId);
        log.info("WebSocketSession to be deleted soon : {}", session);
        
        try {
            
            if(session == null) {
                return;
            }
            
            session.close(CloseStatus.SESSION_NOT_RELIABLE);
            socketSessionMap.remove(userId);
            
        }catch(IOException e) {
            log.info("IO Exception occurred while trying to close the session ");
            
            throw new RuntimeException("IOException on closing websocket session", e);
        }

    }
    
    private void validateOrFail(WebSocketSession session) {
        if(session == null) {
            log.error("Illegal argument. Parameter session is null.");
            throw new IllegalArgumentException("Null value session");
        }else if(session.getPrincipal() == null) {
            log.error("Illegal access detected. Session with no principal detected."
                                                            + "Invalid session : {}", session);
            throw new IllegalStateException("Session with no principal connected");
        }
    }
}
