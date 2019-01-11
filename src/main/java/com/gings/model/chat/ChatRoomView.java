package com.gings.model.chat;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gings.domain.chat.ChatMessage;
import com.gings.domain.chat.ChatRoom.ChatRoomUser;
import com.gings.utils.code.ChatRoomType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class ChatRoomView {
   
    @Getter
    @Setter
    @ToString
    public static class ChatRoomRefreshReq {
        
        @NotEmpty
        List<@Valid PriorChatRoomInfo> chatRoomInfos;
    }
    
    @Getter
    @Setter
    @ToString
    public static class PriorChatRoomInfo {
        
        @Positive
        private int roomId;
        @Positive
        private int latestReceiveMessage;
        @NotEmpty
        private List<Integer> users;
    }
    
    @Getter
    @Setter
    @ToString
    public static class RefreshedChatRoomsStatus {

        List<Integer> deletedChatRooms;
        List<NewChatRoom> newChatRooms;
        List<ExistingChatRoom> existingChatRooms;

    }
    
    @Getter
    @Setter
    @ToString
    public static class ExistingChatRoom {
        
        private int id;
        private int lastReadMessage;
        private int latestReceiveMessage;
        private List<Integer> deletedUsers;
        private List<ChatRoomUser> addedUsers;
        private List<ChatMessage> messages;
        
    }
    
    @Getter
    @Setter
    @ToString
    public static class NewChatRoom {
        
        private int id;
        private ChatRoomType type;
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        private int lastReadMessage;
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        private int latestReceiveMessage;
        private List<ChatRoomUser> users;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<ChatMessage> messages;
    }
   
}
