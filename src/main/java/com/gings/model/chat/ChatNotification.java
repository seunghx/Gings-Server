package com.gings.model.chat;


import com.gings.domain.chat.ChatRoom;

import com.gings.domain.chat.ChatRoom.ChatRoomUser;
import com.gings.model.chat.ChatRoomView.NewChatRoom;
import com.gings.utils.code.ChatNotificationType;
import com.gings.model.chat.ChatRoomView.RefreshedChatRoomsStatus;
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
    
    @Getter
    @Setter
    public static class ChatRoomStatusRefreshNotification extends ChatNotification {
    
        private RefreshedChatRoomsStatus refreshedChatRooms;
        
        public ChatRoomStatusRefreshNotification(RefreshedChatRoomsStatus refreshedChatRooms) {
            super(ChatNotificationType.ROOM_REFRESHED);
            this.refreshedChatRooms = refreshedChatRooms;
        }
    }

}
