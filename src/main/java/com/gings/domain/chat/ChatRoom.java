package com.gings.domain.chat;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gings.domain.User;
import com.gings.utils.code.ChatRoomType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ChatRoom {
    
    private int id;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ChatMessage> messages;
    private List<ChatRoomUser> users;
    private ChatRoomType type;
    
    /**
     * 
     * 뷰에 따라 변화하는 요소이므로 후에 view 단을 위한 dto로 변경 예정
     * (id, name, job, image)를 위해 {@link User} 객체를 모두 받아오는 것도 무리긴하니 
     * (물론 lazy는 있으나) 우선 아래와같이 사용
     * 
     * @author seunghyun
     *
     */
    @Getter
    @Setter
    @ToString
    public static class ChatRoomUser {
        
        private int id;
        private String name;
        private String job;
        private String image;
        
    }
    
}
