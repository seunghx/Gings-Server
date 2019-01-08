package com.gings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gings.domain.chat.ChatRoom;

@Mapper
public interface ChatMapper {
    
    @Insert("INSERT INTO chat_room(type) VALUES(#{chatRoom.type})")
    @Options(useGeneratedKeys = true, keyProperty = "chatRoom.id", keyColumn = "id")
    public void saveChatRoom(ChatRoom chatRoom);
    
    @Insert({"<script>", 
            "insert into chat_user(room_id, user_id) values", 
            "<foreach collection='users' item='userId' separator=', '>"
                    + "(#{roomId}, #{userId})"+
            "</foreach>",
            "</script>"})
    public void saveUsersToRoom(@Param("roomId") int roomId, @Param("users")List<Integer> users);

    
    @Select("SELECT * FROM 2")
    public void findChatRoomByUserIdAndRoomId(@Param("userId") int userId, @Param("roomId") int roomid);
        
    
}
