package com.gings.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gings.security.WebSocketPrincipal;
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
        private UserRole role;
    }
    
    @GetMapping("temp")
    public ResponseEntity<Temp> temp(Temp temp){
        log.error("{}", temp.getRole());
        
        return new ResponseEntity<>(temp, HttpStatus.OK);
    }
    
    @SubscribeMapping("/topic/temp")
    public void stageSubscription(Principal principal, StompHeaderAccessor accessor) {
         
        log.error("Subscription success. principal: {}", principal==null?null:principal.getName());
        log.error("Succeeded subscription id : {}", accessor.getSubscriptionId());
        
    }
    
    @SubscribeMapping("/topic/temp2")
    public void stageSubscription(@AuthenticationPrincipal WebSocketPrincipal principal, StompHeaderAccessor accessor) {
         
        log.error("Subscription success. principal: {}", principal==null?null:principal.toString());
        log.error("Succeeded subscription id : {}", accessor.getSubscriptionId());
        
    }

    @MessageMapping("/topic/temp")
    @SendTo("/topic/temp")
    public ECHO sendStageChatMessage(ECHO echo) {
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
