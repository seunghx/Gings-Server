package com.gings.model.chat;

import com.gings.utils.code.ChatCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatNotification {
    
    private ChatCommand command;
    private int chatRoomId;
    
}
