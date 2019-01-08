package com.gings.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import com.gings.model.chat.ChatOpenReq;
import com.gings.security.WebSocketPrincipal;
import com.gings.service.ChatService;
import com.gings.utils.InvalidChatRoomCapacityException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ChatController {
    
    
    public static final String CHAT_NOTIFICATION_TOPIC = "/queue/chat/notice";
    
    private final ChatService chatService;
    
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    /**
     * 
     * 아래 chat room type에 따른 capacity 검사는 후에 시간 날 경우 class level bean validation constraint
     * 정의해서 변경예정.
     * 
     * <pre> 
     *     openReq.getRoomType().validateCapacityOrFail(openReq.getOpponent().size() + 1); 
     * </pre>
     * 
     */
    @MessageMapping("/chat/create")
    public void createChat(WebSocketPrincipal principal, ChatOpenReq openReq) {
        
        if(openReq.getRoomType().isCapable(openReq.getUsers().size() + 1)) {
            
            log.info("Validation for chat room capacity failed.");
            log.info("Chat room type : {}, User number in request : {}", openReq.getRoomType()
                                                                       , openReq.getUsers().size() + 1);
            throw new InvalidChatRoomCapacityException("Invalid chat room capacity.");
        }

        log.info("Starting to create new chat room.");
        
        chatService.create(principal.getUserId(), openReq);
        
        log.info("Creation chat room succeeded");
    }
    
    @SubscribeMapping("/topic/chatRoom/{roomId}")
    public void onSubscribe(@AuthenticationPrincipal WebSocketPrincipal principal, StompHeaderAccessor accessor) {
        
    }
    
}
