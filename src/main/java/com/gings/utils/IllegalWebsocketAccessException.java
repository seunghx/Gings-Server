package com.gings.utils;

import lombok.Getter;

public class IllegalWebsocketAccessException extends RuntimeException {

    private static final long serialVersionUID = 6578052779877058300L;
    
    @Getter
    private int accessorId;
    
    public IllegalWebsocketAccessException(String message, int accessorId) {
        super(message);
        this.accessorId = accessorId;
    }
}
