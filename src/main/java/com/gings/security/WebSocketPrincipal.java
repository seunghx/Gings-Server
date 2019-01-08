package com.gings.security;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.gings.security.authentication.Authentication;
import com.gings.utils.UserRole;

import java.security.Principal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class WebSocketPrincipal implements Principal{
    
    /**
     * 
     * {@link SimpMessagingTemplate} 를 이용할 때 전달하는 유저의 id 정보는 unique한 문자열 타입이어야함.
     * 
     * 정수값의 id를 문자열로 바꿔 사용해도 되나(현재 gings rdb user table은 user의 auto increment id 사용) 
     * 유저의 identifier를 얻기 위해 컨트롤러 메서드의 인자 타입으로 {@link java.security.Principal}을
     * 사용할 경우 {@link java.security.Principal#getName()}에서는 정수 값을 문자열로, 이를 받은 비즈니스
     * 로직 단에서는 문자열을 다시 int 또는 long 값으로 변환하여 사용해야 하는 번거로움이 있기 때문에 user email을 
     * 사용하기로함. (어차피 유저의 name을 가져오기 위해 db에 접근하는 김에 함께 가져옴.)
     * 
     * {@link AuthenticationPrincipal} 또는 {@link Authentication#getPrincipal}을 이용하는 방법의 경우 바로 
     * 정수 타입 user id를 가져올 수 있으나 이 또한 특정 유저에게 메세지를 보낼 때 정수를 다시 문자열로 바꿔 사용해야함.
     * 
     * ({@link SimpMessagingTemplate#convertAndSendToUser(String, String, Object)})
     * 
     * 
     */
    private Integer userId;
    private String email;
    private String name;
    private UserRole role;
    
    public WebSocketPrincipal(Integer userId, UserRole role, String email, String name) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.userId = userId;
    }
}
