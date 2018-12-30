package com.gings.security.utils;

public class AuthNumberNotificationFailedException extends RuntimeException {

    private static final long serialVersionUID = -5615003352831108223L;
    
    public AuthNumberNotificationFailedException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
