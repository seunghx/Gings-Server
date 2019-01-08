package com.gings.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gings.utils.UserRole;

//import jdk.internal.line.internal.Log;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TempController {
    
    @Getter
    @Setter
    @ToString
    public static class Temp {
    }
    
    
    @SubscribeMapping("/topic/temp")
    public void stageSubscription(Principal principal, StompHeaderAccessor accessor) {
         
        log.error("Succeeded subscription id : {}", accessor.getSubscriptionId());
        
        log.error("{}", principal);
    }
    

    @MessageMapping("/temp")
    @SendTo("/topic/temp")
    public ECHO sendStageChatMessage(Principal principal, ECHO echo) {
        log.error("{}", principal.getClass());
        log.error("{}", principal);
        log.error("Message sending success");
        log.error("Message : {}", echo);
        
        return echo;
    }
    
    
    @Getter
    @Setter
    @ToString
    public static class ECHO {
        String message;
    }
    

}
