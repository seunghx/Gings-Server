package com.gings.utils;

public class InvalidChatRoomCapacityException extends RuntimeException{

    
    private static final long serialVersionUID = 8910675401704100136L;

    public InvalidChatRoomCapacityException(String message) {
        super(message);
    }
    
}
