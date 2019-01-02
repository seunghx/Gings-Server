package com.gings.dao;


import com.gings.domain.Club;
import com.gings.domain.Event;
import com.gings.domain.ClubUser;
import com.gings.model.Pagination;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClubMapper {

    // 클럽 전체 조회 (findAllClub)
    @Select("SELECT club_id as clubId, intro_img as introImg, back_img as backImg " +
            "FROM club LIMIT #{pagination.limit} OFFSET #{pagination.offset}")
    public List<Club> findAllClub(@Param("pagination") final Pagination pagination);


    // 클럽 고유 번호로 클럽 조회(findClubByClubId)
    @Select("SELECT * FROM club WHERE club_id = #{clubId}")
    @Results(value = {
            @Result(property = "introImg",column = "intro_img"),
            @Result(property = "backImg",column = "back_img"),
            @Result(property = "event",column = "club_id", javaType = List.class,
                    many = @Many(select = "findEventByClubId"))
    })
    public Club findClubByClubId(int clubId);

    // 클럽 고유 번호로 이벤트 전체 조회(findEventsByClub)
    @Select("SELECT * FROM event WHERE club_id = #{clubId}")
    @Results(value = {
            @Result(property = "date",column = "date"),
            @Result(property = "time",column = "time"),
            @Result(property = "title",column = "title"),
            @Result(property = "limit",column = "limit_person"),
            @Result(property = "place",column = "place"),
            @Result(property = "eventImg",column = "event_img"),
            @Result(property = "detailImg",column = "detail_img")
    })
    public List<Event> findEventByClubId(int clubId);

}
