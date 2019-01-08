package com.gings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.gings.domain.chat.ChatMessage;
import com.gings.domain.chat.ChatRoom;
import com.gings.domain.chat.ChatRoom.ChatRoomUser;

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
    
    
    @Select("SELECT * FROM chat_room WHERE id = #{roomId}")
    @Results({
        @Result(property = "id", column="id"),
        @Result(property = "type", column="type"),
        @Result(property = "users" , column="id", javaType = List.class, 
                many = @Many(select = "findChatRoomUserByRoomId"))
    })
    public ChatRoom findChatRoomByRoomId(int roomId);
    

    @Select("SELECT * FROM chat_user c JOIN user u ON c.user_id = u.user_id "
          + "WHERE c.room_id = #{roomId}")
    @Results({
        @Result(property = "id", column = "user_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "job", column = "job"),
        @Result(property = "lastReadMessageId", column = "last_read_message"),
        @Result(property = "latestReceiveMessageId", column = "latest_receive_message")
    })
    public List<ChatRoomUser> findChatRoomUserByRoomId(int roomId);    

    
    @Select("SELECT * FROM chat_message "
            + "WHERE room_id = #{roomId} AND write_at > (#{lastMessageId})")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "roomId", column = "room_id"),
        @Result(property = "writerId", column = "writer_id"),
        @Result(property = "type", column = "type"),
        @Result(property = "write_at", column = "writeAt")
    })
    public List<ChatMessage> findChatMessageByRoomId(@Param("roomId") int roomId, 
                                                     @Param("lastMessageId") int lastMessageId);

}
