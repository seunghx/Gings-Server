package com.gings.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gings.controller.ChatController;
import com.gings.dao.ChatMapper;
import com.gings.domain.ChatRoom;
import com.gings.model.chat.ChatNotification;
import com.gings.model.chat.ChatOpenReq;
import com.gings.security.WebSocketPrincipal;
import com.gings.utils.code.ChatCommand;
import com.gings.utils.code.ChatRoomType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatService {
    
    
    private final ChatMapper chatMapper;
    private final SimpMessagingTemplate messagingTemplate;

    
    public ChatService(ChatMapper chatMapper, SimpMessagingTemplate messagingTemplate) {
        this.chatMapper = chatMapper;
        this.messagingTemplate = messagingTemplate;
    }
    
    
    @Transactional
    public void create(int userId, ChatOpenReq openReq) {
                
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setType(openReq.getRoomType());
        
        chatMapper.saveChatRoom(chatRoom);
        
        openReq.getUsers().add(userId);
        chatMapper.saveUsersToRoom(chatRoom.getId(), openReq.getUsers());
        
        sendRoomIdToUser(chatRoom.getId(), openReq.getUsers());
        
        
    }
    
    private void sendRoomIdToUser(int roomId, List<Integer> users) {
        users.forEach(user -> {
            ChatNotification notification = new ChatNotification(ChatCommand.ROOM_OPENED, roomId);
            messagingTemplate.convertAndSendToUser(user.toString(), ChatController.CHAT_NOTIFICATION_TOPIC, 
                                                   notification);
        });
    }
    
}
   