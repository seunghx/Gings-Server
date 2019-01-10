package com.gings.model.chat;


import com.gings.domain.chat.ChatRoom.ChatRoomUser;
import com.gings.utils.code.ChatNotificationType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public abstract class ChatNotification {
    
    private final ChatNotificationType type;
    
    @Getter
    @Setter
    public static class UserEnteredNotification extends ChatNotification {
        
        private int roomId;
        private ChatRoomUser user;
        
        public UserEnteredNotification(int roomId, ChatRoomUser user) {
            super(ChatNotificationType.USER_ENTER);
            this.roomId = roomId;
            this.user = user;
        }
        
        @Override
        public String toString() {
            return super.toString() + "(roomId=" + roomId + ", user=" + user +")";
        }
    }
    
}
