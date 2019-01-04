package com.gings.dao;


import com.gings.domain.Club;
import com.gings.domain.ClubUser;
import com.gings.domain.Event;
import com.gings.domain.EventUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClubMapper {

    // 클럽 전체 조회 (findAllClub)
    @Select("SELECT club_id as clubId, intro_img as introImg, back_img as backImg " +
            "FROM club")
    public List<Club> findAllClub();


    // 클럽 고유 번호로 클럽 조회(findClubByClubId)
    @Select("SELECT * FROM club WHERE club_id = #{clubId}")
    @Results(value = {
            @Result(property = "introImg",column = "intro_img"),
            @Result(property = "backImg",column = "back_img"),
            @Result(property = "event",column = "club_id", javaType = List.class,
                    many = @Many(select = "findEventByClubId")),
            @Result(property = "users",column = "club_id",javaType = List.class,
                    many = @Many(select = "findStatusByClub"))
    })
    public Club findClubByClubId(int clubId);

    // 클럽 고유 번호로 이벤트 전체 조회(findEventsByClub)
    @Select("SELECT * FROM event WHERE club_id = #{clubId}")
    @Results(value = {
            @Result(property = "eventId",column = "event_id"),
            @Result(property = "date",column = "date"),
            @Result(property = "time",column = "time"),
            @Result(property = "title",column = "title"),
            @Result(property = "limit",column = "limit_person"),
            @Result(property = "place",column = "place"),
            @Result(property = "eventImg",column = "event_img"),
            @Result(property = "detailImg",column = "detail_img"),
            @Result(property = "users",column = "event_id",javaType = List.class,
            many = @Many(select = "findStatusByEvent"))
    })
    public List<Event> findEventByClubId(int clubId);

    //이벤트 고유 번호로 이벤트 조회(findEventByEvent)
    @Select("SELECT * FROM event WHERE club_id = #{clubId} AND event_id = #{eventId}")
    @Results(value = {
            @Result(property = "date",column = "date"),
            @Result(property = "time",column = "time"),
            @Result(property = "title",column = "title"),
            @Result(property = "limit",column = "limit_person"),
            @Result(property = "place",column = "place"),
            @Result(property = "eventImg",column = "event_img"),
            @Result(property = "detailImg",column = "detail_img"),
            @Result(property = "users",column = "event_id",javaType = List.class,
                    many = @Many(select = "findStatusByEvent"))
    })
    public Event findEventByEvent(int clubId, int eventId);

    //클럽 고유 번호로 가입여부 조회(findStatusByClub)
    @Select("SELECT user_id as userId, status as users FROM club_user WHERE club_id = #{clubId}")
    public List<ClubUser> findStatusByClub(int clubId);

    //이벤트 고유 번호로 참여여부 조회(findStatusByEvent)
    @Select("SELECT user_id as userId, status as users FROM event_user WHERE event_id = #{eventId}")
    public List<EventUser> findStatusByEvent(int eventId);


    //클럽 가입
    @Insert("INSERT INTO club_user(club_id,user_id,status) VALUES(#{clubId},#{userId},#{clubStatus})")
    void joinClub(int clubId, int userId, String clubStatus);

    //이벤트 가입
    @Insert("INSERT INTO event_user(event_id,user_id,status) VALUES(#{eventId}, #{userId}, #{eventStatus})")
    void joinEvent(int eventId, int userId, String eventStatus);
}
