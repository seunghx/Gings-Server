package com.gings.model.chat;


import com.gings.domain.chat.ChatRoom;
import com.gings.domain.chat.ChatRoom.ChatRoomUser;
import com.gings.model.chat.ChatRoomView.NewChatRoom;
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
    
    @Getter
    @Setter
    public static class UserExitedNotification extends ChatNotification {
        
        private int roomId;
        private int userId;
        
        public UserExitedNotification(int roomId, int userId) {
            super(ChatNotificationType.USER_EXIT);
            this.roomId = roomId;
            this.userId = userId;
        }
        
        @Override
        public String toString() {
            return super.toString() + "(roomId=" + roomId + ", userId=" + userId +")";
        }
    }
    
    @Getter
    @Setter
    public static class ChatOpenedNotification extends ChatNotification {
        
        private NewChatRoom chatRoom;
        
        public ChatOpenedNotification(NewChatRoom chatRoom) {
            super(ChatNotificationType.ROOM_OPENED);
            this.chatRoom = chatRoom;
        }
        
        @Override
        public String toString() {
            return super.toString() + "(chatRoom=" + chatRoom + ")";
        }
    }
    
}
