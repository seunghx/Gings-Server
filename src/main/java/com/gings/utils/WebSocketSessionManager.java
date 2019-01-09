package com.gings.utils;

import org.springframework.web.socket.WebSocketSession;

public interface WebSocketSessionManager {
    public void onConnect(WebSocketSession session);
       
    public void onDisconnect(String userId);
    
    public void close(String userId);
}
