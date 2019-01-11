package com.gings.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gings.dao.ChatMapper;
import com.gings.domain.chat.ChatMessage;
import com.gings.domain.chat.ChatRoom;
import com.gings.domain.chat.ChatRoom.ChatRoomUser;
import com.gings.model.chat.ChatNotification;
import com.gings.model.chat.ChatOpenReq.GroupChatOpenReq;
import com.gings.model.chat.ChatOpenReq.OneToOneChatOpenReq;
import com.gings.model.chat.ChatRoomView.RefreshedChatRoomsStatus;
import com.gings.model.chat.ChatRoomView.ExistingChatRoom;
import com.gings.model.chat.ChatRoomView.NewChatRoom;
import com.gings.model.chat.ChatRoomView.PriorChatRoomInfo;
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
    private final ModelMapper modelMapper = new ModelMapper();

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
        
        validateOneToOneChatOrFail(userId, openReq);
        
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
    
    private void validateOneToOneChatOrFail(int userId, OneToOneChatOpenReq openReq) {
        if(userId == openReq.getOpponentId()) {
            log.warn("Illegal access detected while trying to create new one to one chat room");
            log.warn("Same user id and opponent id : {}", userId);
            
            throw new IllegalWebsocketAccessException("User id is same with opponentId", userId);
        }
        
        // 동일 유저와의 일대일 채팅 신청 예외 처리 예정 -> db 테이블 따로 추가 후 변경사항 적용한 후 처리예정
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
        
        log.info("Newly created chat room to be notified : {}", chatRoom);
        
        sendRoomToUser(modelMapper.map(chatRoom, NewChatRoom.class));
    }
    
    private void sendRoomToUser(NewChatRoom room) {
        
        ChatNotification notification = new ChatOpenedNotification(room);
        
        List<ChatRoomUser> users = room.getUsers();
        
        if(users.size() == 0) {
            log.error("Illegal state detected. Chat room {} has no user.", room);
            return;
        }
        
        users.forEach(user -> {
            messagingTemplate.convertAndSendToUser(String.valueOf(user.getId()), 
                                                   CHAT_NOTIFICATION_TOPIC, notification);
        });
    }
    
    public boolean isUserExistInChatRoom(int userId, int roomId) {
        
        log.info("Checking existence for user {} with room {}", userId, roomId);
        
        if(chatMapper.existByUserIdAndRoomId(roomId, userId)) {
            log.info("Requesting user {} exists in room {}", userId, roomId);
            return true;
        }else {
            log.info("Requesting user {} does not exist in room {}", userId, roomId);
            return false;
        }
        
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
    
    /**
     *      
     * @return - 요청 user {@code userId}에 대하여 기존(이전) 채팅방 상태 정보 {@code chatRooms}를 기반으로
     *           새로 업데이트된 정보를 담은 {@link ChatRoomRefreshRes}를 반환. 만약 empty(null 포함) list가 인자로 전달되었을 
     *           경우 null을 반환.
     * 
     */
    public RefreshedChatRoomsStatus refreshChatRoomHistory(int userId, List<PriorChatRoomInfo> chatRooms) {
        
        if(chatRooms == null) {
            log.error("Parameter chatRooms is null. Checking controller or more prior component required.");
            chatRooms = new ArrayList<>();
        }
        
        log.info("Starting to process refreshing chat room history. Prior chat rooms : {}", chatRooms);
                
        Map<Integer, PriorChatRoomInfo> priorChatRoomMap = new HashMap<>();
        
        chatRooms.stream()
                 .forEach(room -> {
                     priorChatRoomMap.put(room.getRoomId(), room);
                 });   
                     
        List<Integer> currentChatRoomIds = chatMapper.findChatRoomIdsByUserId(userId);
        
        return refreshChatRooms(userId, priorChatRoomMap, currentChatRoomIds);
    }
    
    /**
     * 
     * @param priorChatRoomMap - Mapping for user id to prior chat room info. 
     * @param currentRoomIds - 가장 최신 데이터베이스에 반영된 {@code userId}에 대한 채팅방 정보
     * 
     * @return refresh된 채팅방 상태 정보로 삭제 된 채팅방, 추가된 채팅방 정보와 각 채팅방 별로 마지막 읽은 메세지, 
     *         가장 최근 받은 메세지, 유저 목록 등의 정보 및 전달 받은 인자에 따라 필요할 경우 추가로 전달할 메세지까지
     *         포함한다. (각 채팅방 별로 삭제된 유저 및 추가된 유저에 대한 정보 또한 포함)
     *         
     */
    private RefreshedChatRoomsStatus refreshChatRooms(int userId, 
                                                Map<Integer, PriorChatRoomInfo> priorChatRoomMap, 
                                                List<Integer> currentRoomIds){
        
        List<NewChatRoom> newChatRooms = new ArrayList<>();
        List<ExistingChatRoom> existingChatRooms = new ArrayList<>();
        
        currentRoomIds.stream()
                      .forEach(roomId -> {
                          ChatRoom room = chatMapper.findChatRoomByRoomId(roomId);
                          PriorChatRoomInfo priorChatRoom = priorChatRoomMap.get(roomId);

                          if(priorChatRoom == null) {   // 새로 생성된 room이라면
                             NewChatRoom newRoom = getNewChatRoom(userId, room);
                             
                             newChatRooms.add(newRoom);
                          }else {
                             ExistingChatRoom existingRoom = 
                                              getExistingChatRoom(userId, room, priorChatRoom);
                              
                             existingChatRooms.add(existingRoom);
                          }
                      });
        
        RefreshedChatRoomsStatus res = new RefreshedChatRoomsStatus();
        res.setDeletedChatRooms(getDeletedRoomIds(priorChatRoomMap, currentRoomIds));
        res.setExistingChatRooms(existingChatRooms);
        res.setNewChatRooms(newChatRooms);
        
        log.debug("Refreshing chat rooms info succeeded. Refreshed chat rooms info : {}", res);
        
        return res;
    }
    
    /**
     * 
     * @return 전달 받은 인자 {@code priorChatRooms}와 {@code curChatRooms}를 비교하여 
     *         최근 저장된 채팅방 정보에서 삭제된 정보가 있을 경우 이(삭제된 chat room id의 list)를 반환한다. 
     *         삭제된 채팅방이 없을 경우 빈 list를 반환
     *         
     */
    private List<Integer> getDeletedRoomIds(Map<Integer, PriorChatRoomInfo> priorChatRooms, 
                                            List<Integer> curChatRooms){
        
        Set<Integer> priorChatRoomSet =  new HashSet<>(priorChatRooms.keySet());
                
        priorChatRoomSet.removeAll(curChatRooms);
        
        log.debug("Deleted chat room ids : {}", priorChatRoomSet);
        
        return new ArrayList<>(priorChatRoomSet);                       
    }
    
    /**
     * 
     * 현재 새로 추가된 채팅방(현재 사용 기기가 오프라인일 때 다른 기기 사용 중 새로 생성된 채팅방을 의미) 정보를 전달할 때
     * 모든 chat message를 반환한다. 현재 사용 기기가 아주 오랜시간동안 오프라인이였을 경우 전달해야 할 메세지 수가 많아질 수 
     * 있으므로 후에 이 부분에 대한 추가적인 수정 예정. 
     * 
     * @param room - 유저에게 전달될 반환 클래스 {@link NewChatRoom}에 필요한 데이터를 구성할 현재 채팅방 정보
     * @param priorChatRoom - {@link NewChatRoom} 구성에 필요한 비교 정보로 사용될 기존 채팅방 정보 
     * 
     * @return {@link NewChatRoom} 사용자 입장에서 새로 추가될 채팅방 정보가 담겨있다. 현재 요청 기기 기준으로는 새로생긴
     *         채팅방이지만 다른 기기(모바일 - 브라우저)에서는 이미 생성되고 사용되었던 채팅방을 의미.
     *         
     */
    private NewChatRoom getNewChatRoom(int userId, ChatRoom room) {
        NewChatRoom newRoom = new NewChatRoom();
        
        newRoom.setId(room.getId());
        newRoom.setUsers(room.getUsers());
        newRoom.setType(room.getType());
        
        ChatRoomUser sameUser = room.getUsers()
                                    .stream()
                                    .filter(user -> user.getId() == userId)
                                    .findFirst()
                                    .<IllegalStateException>orElseThrow(() ->{
                                        log.error("Illegal state. User {} does not exist in room {}", room);
                                        
                                        throw new IllegalStateException("User does not exist in chat room ");
                                    });
        
        newRoom.setLastReadMessage(sameUser.getLastReadMessage());
        newRoom.setLatestReceiveMessage(sameUser.getLatestReceiveMessage());
        
        List<ChatMessage> unReceivedMessages = new ArrayList<>();
        
        // 새로 생긴 채팅방에 메세지가 하나도 없을 경우 db를 다녀오는 것은 낭비.
        if(sameUser.getLatestReceiveMessage() != 0) {
            unReceivedMessages = chatMapper.findChatMessageByRoomId(newRoom.getId());
        }

        newRoom.setMessages(unReceivedMessages);
        return newRoom;
        
    }
    
    private ExistingChatRoom getExistingChatRoom(int userId, ChatRoom room, PriorChatRoomInfo priorChatRoom) {
        ExistingChatRoom existingRoom = new ExistingChatRoom();
        
        ChatRoomUser sameUser = room.getUsers()
                                    .stream()
                                    .filter(user -> user.getId() == userId)
                                    .findFirst()
                                    .<IllegalStateException>orElseThrow(() ->{
                                        log.error("Illegal state. User {} does not exist in room {}", room);

                                        throw new IllegalStateException("User does not exist in chat room ");
                                    });
        
        int priorLatestReceiveMessage = priorChatRoom.getLatestReceiveMessage();
        
        if(priorLatestReceiveMessage < sameUser.getLatestReceiveMessage()) {
            existingRoom.setMessages(
                chatMapper.findChatMessageByRoomIdAndLatestMessage(room.getId(), 
                                                                   priorLatestReceiveMessage));
        }
        
        existingRoom.setId(room.getId());
        existingRoom.setDeletedUsers(getDeletedUser(priorChatRoom.getUsers(), room.getUsers()));
        existingRoom.setAddedUsers(getNewUsers(priorChatRoom.getUsers(), room.getUsers()));
        existingRoom.setLastReadMessage(sameUser.getLastReadMessage());
        existingRoom.setLatestReceiveMessage(sameUser.getLatestReceiveMessage());
        
        return existingRoom;
    }
    
    private List<Integer> getDeletedUser(List<Integer> priorUsers, List<ChatRoomUser> existingUsers){
        
        Set<Integer> priorUserSet = new HashSet<>(priorUsers);
        
        existingUsers.stream()
                     .forEach(existingUser -> priorUserSet.remove(existingUser.getId()));
        
        return new ArrayList<>(priorUserSet);
    }
    
    private List<ChatRoomUser> getNewUsers(List<Integer> priorUsers, List<ChatRoomUser> existingUsers){
        
        Map<Integer, ChatRoomUser> existingUserMap = new HashMap<>();
        
        existingUsers.stream()
                     .forEach(existingUser -> {
                         existingUserMap.put(existingUser.getId(), existingUser);
                     });
        
        priorUsers.stream().forEach(priorUser -> existingUserMap.remove(priorUser));
        
        return new ArrayList<>(existingUserMap.values());
    }
}