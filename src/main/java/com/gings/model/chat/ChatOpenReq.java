package com.gings.model.chat;


import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import com.gings.domain.chat.ChatRoom;
import com.gings.utils.code.ChatRoomType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ChatOpenReq {
    
    private final ChatRoomType roomType;
   
    public ChatRoom getChatRoom() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setType(roomType);
        
        return chatRoom;
    }
    
    @Getter
    @Setter
    public static class OneToOneChatOpenReq extends ChatOpenReq {
        
        @Positive
        private int opponentId;

        public OneToOneChatOpenReq() {
            super(ChatRoomType.OneToOne);
        }
        
    }
    
    @Getter
    @Setter
    public static class GroupChatOpenReq extends ChatOpenReq {
        
        @NotEmpty
        private List<Integer> users; 
        
        public GroupChatOpenReq() {
            super(ChatRoomType.Group);
        }
    }
}
