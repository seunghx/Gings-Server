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
import org.apache.ibatis.annotations.Update;

import com.gings.domain.chat.ChatMessage;
import com.gings.domain.chat.ChatRoom;
import com.gings.domain.chat.ChatRoom.ChatRoomUser;

/**
 * 
 * n+1 select을 피하기 위해 후에 아래 클래스를 xml로 변경 해서 nested result 사용 예정 
 * 
 * {@link #findChatRoomsByUserId(int)
 * 
 * 
 * @author seunghyun
 *
 */
@Mapper
public interface ChatMapper {

    
    // @Select
    // ==============================================================================================

    
    @Select("SELECT * FROM chat_room WHERE id = #{roomId}")
    @Results({
        @Result(property = "id", column="id"),
        @Result(property = "type", column="type"),
        @Result(property = "users" , column="id", javaType = List.class, 
                many = @Many(select = "findChatRoomUserByRoomId"))
    })
    public ChatRoom findChatRoomByRoomId(int roomId);
    
    @Select("SELECT * FROM chat_room "
          + "WHERE id IN (SELECT room_id FROM chat_user WHERE user_id = #{userId})")
    @Results({
        @Result(property = "id", column="id"),
        @Result(property = "type", column="type"),
        @Result(property = "users" , column="id", javaType = List.class, 
                many = @Many(select = "findChatRoomUserByRoomId"))
    })
    public List<ChatRoom> findChatRoomsByUserId(int userId);
    
    
    @Select("SELECT * FROM chat_user c JOIN user u ON c.user_id = u.user_id "
          + "WHERE c.room_id = #{roomId}")
    @Results({
        @Result(property = "id", column = "user_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "job", column = "job"),
        @Result(property = "image", column = "image"),
        @Result(property = "lastReadMessage", column = "last_read_message"),
        @Result(property = "latestReceiveMessage", column = "latest_receive_message")
    })
    public List<ChatRoomUser> findChatRoomUserByRoomId(int roomId);    

    
    @Select("SELECT * FROM chat_message "
            + "WHERE room_id = #{roomId} ORDER BY write_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "roomId", column = "room_id"),
        @Result(property = "writerId", column = "writer_id"),
        @Result(property = "type", column = "type"),
        @Result(property = "write_at", column = "writeAt")
    })
    public List<ChatMessage> findChatMessageByRoomId(int roomId);
    
    @Select("SELECT * FROM chat_message "
          + "WHERE room_id = #{roomId} AND write_at > "
          + "(SELECT write_at FROM chat_message WHERE id = #{lastMessageId}) "
          + "ORDER BY write_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "roomId", column = "room_id"),
        @Result(property = "writerId", column = "writer_id"),
        @Result(property = "type", column = "type"),
        @Result(property = "write_at", column = "writeAt")
    })
    public List<ChatMessage> 
                    findChatMessageByRoomIdAndLatestMessage(@Param("roomId") int roomId, 
                                                            @Param("lastMessageId") int lastMessageId);
    
    @Select("SELECT count(*)>0 FROM chat_user WHERE room_id = #{roomId} AND user_id = #{userId}")
    boolean existByUserIdAndRoomId(@Param("roomId")int roomId, @Param("userId")int userId);
    
    
    @Select("SELECT last_read_message FROM chat_user WHERE room_id = #{roomId} AND user_id = #{userId}")
    public int readLastReadMessage(@Param("roomId") int roomId, @Param("userId")int userId);
    
    
    @Select("SELECT latest_receive_message FROM chat_user WHERE room_id = #{roomId} AND user_id = #{userId}")
    public int readLatestReceiveMessage(@Param("roomId") int roomId, @Param("userId")int userId);
    
    @Select("SELECT room_id from chat_user WHERE user_id = #{userId}")
    public List<Integer> findChatRoomIdsByUserId(@Param("userId") int userId);
    
    
    // @Insert
    // ==============================================================================================
    
    
    @Insert("INSERT INTO chat_room(type) VALUES(#{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public void saveChatRoom(ChatRoom chatRoom);
    
    @Insert({"<script>", 
                "INSERT INTO chat_user(room_id, user_id) VALUES", 
                    "<foreach collection='users' item='userId' separator=', '>"
                        + "(#{roomId}, #{userId})"+
                    "</foreach>",
            "</script>"})
    public void saveUsersToRoom(@Param("roomId") int roomId, @Param("users")List<Integer> users);
    
    
    @Insert("INSERT INTO chat_message(room_id, writer_id, write_at, type, message) "
          + "VALUES(#{roomId}, #{writerId}, #{writeAt}, #{type}, #{message})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public void saveMessage(ChatMessage chatMessage);
    
    
    // @Update
    // ==============================================================================================
    
    
    @Update("UPDATE chat_user SET latest_receive_message = #{messageId} "
          + "WHERE user_id = #{userId} AND room_id = #{roomId}")
    public void updateLastReadMessage(@Param("userId") int userId, @Param("roomId") int roomId, 
                                      @Param("messageId")int messageId);
    
    
    @Update("UPDATE chat_user SET last_read_message = #{messageId} "
            + "WHERE user_id = #{userId} AND room_id = #{roomId}")
    public void updateLatestReceived(@Param("userId") int userId, @Param("roomId") int roomId,  
                                     @Param("messageId") int messageId);
    
    
}
