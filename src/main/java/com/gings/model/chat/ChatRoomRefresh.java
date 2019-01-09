package com.gings.model.chat;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import com.gings.domain.chat.ChatMessage;
import com.gings.domain.chat.ChatRoom.ChatRoomUser;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class ChatRoomRefresh {
    
    @Getter
    @Setter
    @ToString
    public static class PriorChatRoomInfo {
        
        @Positive(message = "값이 올바르지 않습니다.")
        private int roomId;
        @Positive(message = "값이 올바르지 않습니다.")
        private int latestReceivedMessage;
        @NotEmpty(message = "기존 채팅방 유저 정보를 입력하세요.")
        private List<Integer> users;
    
    }
    
    @Getter
    @Setter
    @ToString
    public static class ChatRoomRefreshReq {
        
        @NotEmpty(message = "기존 채팅방 정보를 입력하세요.")
        List<@Valid PriorChatRoomInfo> chatRoomInfos;
        
    }
        
    @Getter
    @Setter
    @ToString
    public static class NewerChatRoomInfo {
        
        private int lastReadMessage;
        private int latestReceivedMessage;
        private List<Integer> deletedUsers;
        private List<ChatRoomUser> addedUsers;
        private List<ChatMessage> messages;
        
    }
    
    @Getter
    @Setter
    @ToString
    public static class ChatRoomRefreshRes {

        List<ChatRoomRefreshRes> chatRoomInfos;
    }
   
}
