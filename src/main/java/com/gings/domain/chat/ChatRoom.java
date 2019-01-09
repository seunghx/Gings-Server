package com.gings.domain.chat;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gings.domain.User;
import com.gings.utils.code.ChatRoomType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ChatRoom {
    
    private int id;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ChatMessage> messages;
    private List<ChatRoomUser> users;
    private ChatRoomType type;
    
    @Getter
    @Setter
    @ToString
    public static class ChatRoomUser {
        
        private int id;
        private String name;
        private String job;
        
        private int lastReadMessageId;
        private int latestReceiveMessageId;
        
    }
}
