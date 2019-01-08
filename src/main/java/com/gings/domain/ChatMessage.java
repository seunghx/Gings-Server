package com.gings.domain;

import java.time.LocalDateTime;

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
    private int count;
    private LocalDateTime writeAt;
    private MessageType type;
    
}
