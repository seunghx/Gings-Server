package com.gings.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gings.dao.ChatMapper;
import com.gings.domain.chat.ChatMessage;
import com.gings.domain.chat.ChatRoom;
import com.gings.domain.chat.ChatRoom.ChatRoomUser;
import com.gings.model.chat.ChatNotification;
import com.gings.model.chat.ChatOpenReq;
import com.gings.model.chat.ChatOpenReq.GroupChatOpenReq;
import com.gings.model.chat.ChatOpenReq.OneToOneChatOpenReq;
import com.gings.model.chat.IncomingMessage;
import com.gings.model.chat.ChatNotification.ChatOpenedNotification;
import com.gings.utils.IllegalWebsocketAccessException;

import lombok.extern.slf4j.Slf4j;

import static com.gings.controller.ChatController.CHAT_NOTIFICATION_TOPIC;


@Slf4j
@Service
public class ChatService {
    
    
    private final ChatMapper chatMapper;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Value("${gings.message.admin-sender-id}")
    private String GINGS_ADMIN;

    
    public ChatService(ChatMapper chatMapper, SimpMessagingTemplate messagingTemplate) {
        this.chatMapper = chatMapper;
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * {@link Transactional} 때문에 notification을 메서드 밖에서 수행.
     * 
     * @return created chat room id;
     */
    @Transactional
    public int initOneToOneChatRoom(int userId, OneToOneChatOpenReq openReq) {
                
        ChatRoom chatRoom = openReq.getChatRoom();
        
        chatMapper.saveChatRoom(chatRoom);
        
        log.info("Saving chat room info succeeded. Now save chat room user info.");
        
        List<Integer> userIds = new ArrayList<>();
        userIds.add(openReq.getOpponentId());
        userIds.add(userId);
        
        chatMapper.saveUsersToRoom(chatRoom.getId(), userIds);
        
        log.info("Creating chat room succeeded.");
        
        return chatRoom.getId();
    }
    
    @Transactional
    public int initGroupChatRoom(int userId, GroupChatOpenReq openReq) {
                
        ChatRoom chatRoom = openReq.getChatRoom();
        
        chatMapper.saveChatRoom(chatRoom);
        
        log.info("Saving chat room info succeeded. Now save chat room user info.");
        
        List<Integer> userIds = openReq.getUsers();
        userIds.add(userId);
        
        chatMapper.saveUsersToRoom(chatRoom.getId(), userIds);
        
        log.info("Creating chat room succeeded.");
        
        return chatRoom.getId();
    }
            
    /**
     * {@link Transactional}의 영향 받지 않기 위해 분리.
     */
    public void notifyChatRoomOpening(int roomId) {
        
        log.info("Starting to notify chat room opening for room : {}", roomId);
        
        ChatRoom chatRoom = chatMapper.findChatRoomByRoomId(roomId);
        
        sendRoomToUser(chatRoom);
    }
    
    private void sendRoomToUser(ChatRoom room) {
        
        ChatNotification notification = new ChatOpenedNotification(room);
        
        List<ChatRoomUser> users = room.getUsers();
        
        if(users.size() == 0) {
            log.error("Illegal state detected. Chat room has no user.");
            return;
        }
        
        users.forEach(user -> {
            messagingTemplate.convertAndSendToUser(String.valueOf(user.getId()), 
                                                   CHAT_NOTIFICATION_TOPIC, notification);
        });
    }
    
    public boolean isUserExistInChatRoom(int userId, int roomId) {
        
        log.info("Checking existence for user {} with room {}", userId, roomId);
        
        return chatMapper.existByUserIdAndRoomId(userId, roomId);
    }
    
    
    public ChatMessage addMeesageToChatRoomAndGet(int userId, int roomId, IncomingMessage message) {
        
        log.info("Starting to save new chat message.");
        
        ChatMessage chatMessage = getChatMessage(userId, roomId, message);
        
        chatMapper.saveMessage(chatMessage);
        
        log.info("New message creation succeeded.");
        
        return chatMessage;
    }
    
    private ChatMessage getChatMessage(int userId, int roomId, IncomingMessage message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setWriterId(userId);
        chatMessage.setRoomId(roomId);
        chatMessage.setType(message.getType());
        chatMessage.setMessage(message.getMessage());
        chatMessage.setWriteAt(LocalDateTime.now());
        
        return chatMessage;
    }
    
    
    public void confirmLastRead(int roomId, int userId, int lastReadMessage) {
        
        validateLastReadOrFail(userId, roomId, lastReadMessage);
        
        log.info("Starting to confirm last read message for message : {}", lastReadMessage);
        
        chatMapper.updateLastReadMessage(userId, roomId, lastReadMessage);
        
        log.info("Confirming message succeeded.");
    }
    
    private void validateLastReadOrFail(int roomId, int userId, int lastRead) {
        int priorLastRead = chatMapper.readLastReadMessage(roomId, userId);
        
        if(lastRead < priorLastRead) {
            log.warn("Illegal access detected. Paramater lastRead {} is smaller than prior vallue {}"
                    , lastRead, priorLastRead);
            
            log.info("User committing illegal access : {}", userId);
            
            throw new IllegalWebsocketAccessException("lastRead message id is smaller than prior one.", 
                                                      userId);
        }
    }
    
    public void confirmLatestReceive(int roomId, int userId, int latestReceiveMessage) {
        
        validateLatestReceiveOrFail(userId, roomId, latestReceiveMessage);
        
        log.info("Starting to confirm latest receive message for message : {}", latestReceiveMessage);
        
        chatMapper.updateLatestReceived(userId, roomId, latestReceiveMessage);
        
        log.info("Confirming message id succeeded.");

    }
    
    private void validateLatestReceiveOrFail(int roomId, int userId, int latestReceive) {
        int priorLatestReceive = chatMapper.readLatestReceiveMessage(roomId, userId);
        
        if(latestReceive < priorLatestReceive) {
            log.warn("Illegal access detected. Parameter latestReceive {} is smaller than prior value {}"
                    , latestReceive, priorLatestReceive);
            
            log.info("User committing illegal access : {}", userId);
            
            throw new IllegalWebsocketAccessException("latestReceive message id is smaller than prior one.", 
                                                      userId);
        }
    }
    
}
   
