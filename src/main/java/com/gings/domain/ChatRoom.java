package com.gings.domain;

import java.util.List;

import com.gings.utils.code.ChatRoomType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ChatRoom {
    
    private int id;
    private List<ChatMessage> messages;
    private List<User> users;
    private ChatRoomType type;
    
}
