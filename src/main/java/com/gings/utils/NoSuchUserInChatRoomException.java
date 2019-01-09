package com.gings.utils;

public class NoSuchUserInChatRoomException extends RuntimeException {

    private static final long serialVersionUID = 8174846296593586014L;

    public NoSuchUserInChatRoomException(String message) {
        super(message);
    }
    
}
