package com.gings.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PushNotification {
    
    public int id;
    public int userId;
    //public NotificationType notificationType;
    public String NotificationType;
    public String message;
    public boolean confirmed;
    public LocalDateTime time;
    
}
