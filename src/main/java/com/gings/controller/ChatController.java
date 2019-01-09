package com.gings.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import com.gings.domain.chat.ChatMessage;
import com.gings.model.chat.ChatOpenReq;
import com.gings.model.chat.IncomingMessage;
import com.gings.model.chat.MessageConfirm.LastReadConfirm;
import com.gings.model.chat.MessageConfirm.LatestReceivedConfirm;
import com.gings.service.ChatService;
import com.gings.utils.InvalidChatRoomCapacityException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ChatController {
    
    public static final String CHAT_NOTIFICATION_TOPIC = "/queue/chat-notice";
    
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
    public void createChat(Principal principal, ChatOpenReq openReq) {
        
        if(openReq.getRoomType().isCapable(openReq.getUsers().size() + 1)) {
            
            log.info("Validation for chat room capacity failed.");
            log.info("Chat room type : {}, User number in request : {}", openReq.getRoomType()
                                                                       , openReq.getUsers().size() + 1);
            
            // 후에 AOP 이용해서 ERROR 메세지 혹은 connection 끊을 예정.
            throw new InvalidChatRoomCapacityException("Invalid chat room capacity.");
        }

        log.info("Starting to create new chat room.");
        
        int roomId = chatService.initChatRoom(Integer.valueOf(principal.getName()), openReq);
        chatService.notifyChatRoomOpening(roomId);
      
    }
    
    @SubscribeMapping("/topic/room/{roomId}")
    public void logSubscription(Principal principal, @DestinationVariable("roomId") int roomId) {
        int userId = Integer.valueOf(principal.getName());
        
        if(chatService.isUserExistInChatRoom(userId, roomId)) {
            log.info("User {} subscribe to chat room {}", userId, roomId);
        }else {
            log.warn("Invalid access detected. User {} subscribe to chat room {}", userId, roomId);
            
            // 후에 connection 종료 로직 추가.
        }
    }
    
    @MessageMapping("/room/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage routeChatMessage(Principal principal, @DestinationVariable("roomId") int roomId, 
                                        IncomingMessage message) {
        
        log.info("Received new message from {} to {}.");
        
        int userId = Integer.valueOf(principal.getName());
        
        ChatMessage newMessage = chatService.addMeesageToChatRoomAndGet(userId, roomId, message);
        
        log.info("Now, routing new message {} to chat room {}", newMessage, roomId);
        
        return newMessage;
    }
    
    @MessageMapping("/room/{roomId}/last-read")
    public void confirmLastRead(Principal principal, 
                                @DestinationVariable("roomId") int roomId,
                                @Validated LastReadConfirm messageConfirm) {
        
        int userId = Integer.valueOf(principal.getName());
        
        log.info("Received message for confirming last read message. room : {}, user :{}", 
                  roomId, userId);
        
        
        chatService.confirmLastRead(userId, roomId, messageConfirm);
        
    }
    
    @MessageMapping("/room/{roomId}/latest-receive")
    public void confirmLatestReceive(Principal principal,
                                     @DestinationVariable("roomId") int roomId,
                                     @Validated LatestReceivedConfirm messageConfirm) {
        
        int userId = Integer.valueOf(principal.getName());
        
        log.info("Received message for confirming latest received message. room : {}, user :{}", 
                  roomId, userId);
        
        chatService.confirmLatestReceive(userId, roomId, messageConfirm);

    }
    
}
