package com.gings.domain;

import java.time.LocalDateTime;

import com.gings.utils.code.NotificationType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PushNotification {
    
    private int id;
    private int userId;
    private NotificationType notificationType;
    private String message;
    private boolean confirmed;
    private LocalDateTime notifyTime;
    
}
