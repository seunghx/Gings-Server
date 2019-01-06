package com.gings.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gings.domain.PushNotification;
import com.gings.model.DefaultRes;
import com.gings.model.PushNotificationReceipt;
import com.gings.security.WebSocketPrincipal;
import com.gings.service.PushNotificationService;

import lombok.extern.slf4j.Slf4j;

// 후에 exception handling은 aop로
@RestController
@Slf4j
public class PushNotificationController {
        
    private final MessageSource messageSource;
    private final PushNotificationService notificationService;

    public PushNotificationController(MessageSource messageSource, 
                                      PushNotificationService notificationService) {
        
        this.notificationService = notificationService;
        this.messageSource = messageSource;
    }
    
    @SubscribeMapping("/notification/newer")
    @SendTo("/queue/notification")
    public List<PushNotification> 
            getNewerPushNotification(@AuthenticationPrincipal WebSocketPrincipal user,
                                     int olderNotificationId) {
        
        List<PushNotification> notifications = 
                notificationService.getNewerPushNotifications(user.getUserId(), olderNotificationId);
                
        return notifications;
    }
    
    /**
     * 
     * push notification 확인 요청 처리.
     * 
     * HTTP PUT method를 이용해 구현해도 상관없긴하나 현재 push notification을 STOMP protocol을 
     * 기반으로 진행하고 있기 때문에 push notification 확인 처리 또한 동일하게 STOMP로 진행하기로 하였다.
     * (후에 STOMP RECEIPT frame사용을 고려해볼 생각)
     * 
     * 
     */
    @MessageMapping("/notification/confirm")
    @SendTo("/queue/notification")
    public PushNotificationReceipt confirmMessage(@AuthenticationPrincipal WebSocketPrincipal user,
                                                  int notificationId){
        
        
        notificationService.confirmNotifications(notificationId, user.getUserId());
        
        if(log.isInfoEnabled()) {
            log.info("Now send receipt message for notification confirming to user : {}", 
                                                                                user.getUserId());
        }
        
        return new PushNotificationReceipt(notificationId);
    }
}
