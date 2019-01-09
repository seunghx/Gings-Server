package com.gings.domain.chat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gings.utils.code.MessageType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessage {
    
    private int id;
    private int roomId;
    private int writerId;
    private String message;
    private LocalDateTime writeAt;
    private MessageType type;
   
}
