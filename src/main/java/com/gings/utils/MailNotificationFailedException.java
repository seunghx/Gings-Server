package com.gings.utils;

/**
 * 
 * {@link javax.mail.MessagingException}이 발생하면 이를 {@link RuntimeException}으로
 * 포장하기 위하여 정의한 예외이다.
 * 
 * @author leeseunghyun
 *
 */
public class MailNotificationFailedException extends RuntimeException {

    private static final long serialVersionUID = 7607031132531245294L;

    public MailNotificationFailedException(String msg) {
        super(msg);
    }

    public MailNotificationFailedException(String msg, Throwable cause) {
        this(msg);
        initCause(cause);
    }
}
