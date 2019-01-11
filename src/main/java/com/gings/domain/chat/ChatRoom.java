package com.gings.domain.chat;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gings.domain.User;
import com.gings.utils.code.ChatRoomType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * {@link @JsonInclude}와 같은 어노테이션은 뷰에 전달되는 DTO에 사용되나 
 * 편의를 위해 도메인 클래스 {@link ChatRoom}에 추가하였다.
 * 
 * 후에 1:1 채팅용 db 테이블 따로 만들고(동일한 일대일 채팅방 생성 편하게 막기위해) 필요할 경우 변경가능.
 * 
 * @author seunghyun
 *
 */
@Getter
@Setter
@ToString
public class ChatRoom {
    
    private int id;
    private List<ChatMessage> messages;
    private List<ChatRoomUser> users;
    private ChatRoomType type;
    
    /**
     * 
     * 뷰에 따라 변화하는 요소가 있을 수 있으므로 후에 view 단을 위한 dto 추가후 변경 예정
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
        @JsonIgnore
        private int lastReadMessage;
        @JsonIgnore
        private int latestReceiveMessage;
    }
}
