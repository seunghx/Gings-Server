package com.gings.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import com.gings.domain.chat.ChatMessage;
import com.gings.model.chat.ChatNotification.ChatRoomStatusRefreshNotification;
import com.gings.model.chat.ChatOpenReq.GroupChatOpenReq;
import com.gings.model.chat.ChatOpenReq.OneToOneChatOpenReq;
import com.gings.model.chat.ChatRoomView.ChatRoomRefreshReq;
import com.gings.model.chat.ChatRoomView.RefreshedChatRoomsStatus;
import com.gings.model.chat.IncomingMessage;
import com.gings.model.chat.MessageConfirm.LastReadConfirm;
import com.gings.model.chat.MessageConfirm.LatestReceiveConfirm;
import com.gings.service.ChatService;
import com.gings.utils.InvalidChatRoomCapacityException;
import com.gings.utils.WebSocketSessionManager;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 *
 * 현재 gings 다른 클래스들과 동일하게 하려고 websocket user name을 {@link User}의 {@code id}로 사용하였는데,
 * int 타입과 String 타입 간의 변환이 많아 불편하여 후에 email로 변경 예정.
 *
 * 채팅방 퇴장 만들기.
 * 
 * @author seunghyun
 *
 */
@Slf4j
@RestController
public class ChatController {

    public static final String CHAT_NOTIFICATION_TOPIC = "/queue/chat-notice";

    private final ChatService chatService;
    private final WebSocketSessionManager sessionManager;

    public ChatController(ChatService chatService, WebSocketSessionManager sessionManager) {
        this.chatService = chatService;
        this.sessionManager = sessionManager;
    }

    //@SubscribeMapping
    //==============================================================================================


    @SubscribeMapping(CHAT_NOTIFICATION_TOPIC)
    public void logChatNotificationSubscription(Principal principal) {
        int userId = Integer.valueOf(principal.getName());

        log.info("User {} subscribe to chat notification queue.", userId);

    }

    @SubscribeMapping("/topic/room/{roomId}")
    public void logChatRoomSubscription(Principal principal, @DestinationVariable("roomId") int roomId) {
        int userId = Integer.valueOf(principal.getName());

        if(chatService.isUserExistInChatRoom(userId, roomId)) {
            log.info("User {} subscribe to chat room {}", userId, roomId);
        }else {
            log.warn("Invalid access detected. User {} subscribe to chat room {}", userId, roomId);

            sessionManager.close(String.valueOf(userId));
        }
    }

    //@MessageMapping
    //==============================================================================================

    /**
     *
     *
     *
     * 아래 chat room type에 따른 capacity 검사는 후에 시간 날 경우 class level bean validation constraint
     * 정의해서 변경예정.
     *
     * <pre>
     *     openReq.getRoomType().validateCapacityOrFail(openReq.getOpponent().size() + 1);
     * </pre>
     *
     *
     *
     */
    @MessageMapping("/chat/create")
    public void createOneToOneChat(Principal principal, @Validated OneToOneChatOpenReq openReq) {

        log.info("Starting to create new chat room.");

        int roomId = chatService.initOneToOneChatRoom(Integer.valueOf(principal.getName()), openReq);

        chatService.notifyChatRoomOpening(roomId);

    }

    @MessageMapping("/group-chat/create")
    public void createGroupChat(Principal principal, @Validated GroupChatOpenReq openReq) {

        if(openReq.getRoomType().isCapable(openReq.getUsers().size() + 1)) {

            log.info("Validation for chat room capacity failed.");
            log.info("Chat room type : {}, User number in request : {}", openReq.getRoomType()
                                                                       , openReq.getUsers().size() + 1);

            sessionManager.close(principal.getName());

            throw new InvalidChatRoomCapacityException("Invalid chat room capacity.");
        }

        log.info("Starting to create new group chat room.");
        
        int roomId = chatService.initGroupChatRoom(Integer.valueOf(principal.getName()), openReq);
        
        chatService.notifyChatRoomOpening(roomId);
      
    }
        
    @MessageMapping("/room/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage routeChatMessage(Principal principal, @DestinationVariable("roomId") int roomId, 
                                        IncomingMessage message) {
        
        int userId = Integer.valueOf(principal.getName());
        
        log.info("Received new message from {} to chat room {}.", userId, roomId);
        
        ChatMessage newMessage = chatService.addMeesageToChatRoomAndGet(userId, roomId, message);
        
        log.info("Now, routing new message {} to chat room {}", newMessage, roomId);
        
        return newMessage;
    }
    
    @MessageMapping("/room/{roomId}/last-read-message")
    public void confirmLastRead(Principal principal, 
                                @DestinationVariable("roomId") int roomId,
                                LastReadConfirm confirm) {
        
        int userId = Integer.valueOf(principal.getName());
        
        log.info("Received message for confirming last read message. room : {}, user :{}", 
                  roomId, userId);
        
        chatService.confirmLastRead(roomId, userId, confirm.getMessageId());
        
    }
    
    @MessageMapping("/room/{roomId}/latest-receive-message")
    public void confirmLatestReceive(Principal principal,
                                     @DestinationVariable("roomId") int roomId,
                                     LatestReceiveConfirm confirm) {
        
        int userId = Integer.valueOf(principal.getName());
        
        log.info("Received message for confirming latest received message. room : {}, user :{}", 
                  roomId, userId);
        
        chatService.confirmLatestReceive(roomId, userId, confirm.getMessageId());

    }
    
    /**
     * 
     * @param refreshReq - bean validation을 위해 정의한 클래스이므로 아래 메서드에서 
     *                     {@code refreshReq#getChatRoomInfos()}를 호출하여 서비스에 전달함. 
     *                     
     * 
     */
    @MessageMapping("/chat/refresh")
    @SendTo(CHAT_NOTIFICATION_TOPIC)
    public ChatRoomStatusRefreshNotification chatRoomRefresh(Principal principal, ChatRoomRefreshReq refreshReq) {
        
       int userId = Integer.valueOf(principal.getName()); 
        
       log.info("Received message for refreshing chat room list history from user : {}.", userId);
       
       RefreshedChatRoomsStatus refreshed = chatService.refreshChatRoomHistory(userId, refreshReq.getChatRoomInfos());
       
       log.debug("Refreshed chat rooms status : {}", refreshed);
       
       return new ChatRoomStatusRefreshNotification(refreshed);
    }    
   
}
