package com.gings.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PushNotificationReceipt {
    private int notificationId;
    
    public PushNotificationReceipt(int notificationId) {
        this.notificationId = notificationId;
    }
}
