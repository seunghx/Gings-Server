package com.gings.model.chat;

import com.gings.utils.code.MessageType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SimpleMessage {
    private MessageType type;
    private String message;
}

