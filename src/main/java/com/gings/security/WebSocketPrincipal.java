package com.gings.security;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.gings.security.authentication.Authentication;
import com.gings.utils.code.UserRole;

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
     * email을 {@link Principal#getName()}이 return하게 함으로써 user를 유일하게 identification
     * 가능하며 unique id(RDB row의 id)를 위 메서드의 반환값으로 사용하려면 일일이 String 타입으로 변환하거나
     * 다시 변환받아야하는 불편이 생긴다. 그러나 gings의 대부분의 DAO 메서드들이 id 기반으로 작성되었기 때문에 
     * 만에 하나 user id를 얻기 위해 user email로 select를 하거나 email을 인자로 받는 mapper 메서드를 새로
     * 
     * 
     */
    private Integer userId;
    private String email;
    private UserRole role;
    
    public WebSocketPrincipal(Integer userId, UserRole role, String email) {
        this.email = email;
        this.role = role;
        this.userId = userId;
    }
    
    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
