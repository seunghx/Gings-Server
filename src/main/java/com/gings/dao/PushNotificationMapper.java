package com.gings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gings.domain.PushNotification;

@Mapper
public interface PushNotificationMapper {
    
    @Select("SELECT id, user_id as userId, notification_type as notificationType, "
                 + "confirmed, notify_time as notifyTime"
          + "FROM push_notification "
          + "WHERE user_id = #{userId} "
          + "AND notify_time > (SELECT notify_time FROM push_notification "
          + "WHERE id = #{olderId})")
    public List<PushNotification> findNewerPushNotification(@Param("olderId") int olderId, 
                                                            @Param("userId") int userId);
    
    @Update("UPDATE push_notification SET confirmed = 1 "
          + "WHERE id = #{notificationId} AND user_id = #{userId}")
    public void updateNotificationConfirmation(@Param("notificationId") int notificationId, 
                                               @Param("userId") int userId);
     

    @Insert("INSERT INTO "
              + "push_notification(user_id, notification_type, message) "
          + "VALUES"
              + "(#{notification.userId}, #{notification.notificationType}, #{notification.message}")
    @Options(useGeneratedKeys = true, keyProperty = "notification.id", keyColumn="id")
    public void save(PushNotification notification);
    
}
