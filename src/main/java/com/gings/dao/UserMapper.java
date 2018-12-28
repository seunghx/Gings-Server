package com.gings.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.One;

import java.util.List;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.FetchType;

import com.gings.domain.Introduce;
import com.gings.domain.Signature;
import com.gings.domain.User;
import com.gings.domain.UserKeyword;

@Mapper
public interface UserMapper {
    
    /**
     * {@link User} 조회 
     */
    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    @Results(value= {
            @Result(property="id", column="user_id"), @Result(property="email", column="email"),
            @Result(property="name", column="name"),  @Result(property="pwd", column="pwd"),
            @Result(property="region", column="region"), @Result(property="job", column="job"),
            @Result(property="company", column="company"), @Result(property="field", column="field"),
            @Result(property="status", column="status"),  @Result(property="role", column="role"),
            @Result(property="image", column="image"),
            @Result(property="coworkingEnabled", column="coworking_chk"),
            @Result(property="introduce", column="user_id", javaType=Introduce.class,
                    one=@One(select="findIntroduceByUserId")),
            @Result(property="keywords", column="user_id", javaType=List.class,
                    many=@Many(select="findKeywordsByUserId")),
            @Result(property="signatures", column="user_id", javaType=List.class,
                    many=@Many(select="findSignaturesByUserId", fetchType=FetchType.LAZY))
    })
    public User findByUserId(int userId);

    /**
     * {@link Introduce} 조회
     */
    @Select("SELECT * from introduce WHERE user_id = #{userId}")
    @Results({
            @Result(property="id", column="introduce_id"),
            @Result(property="content", column="content"),
            @Result(property="imgs", column="introduce_id", javaType=List.class,
                    many=@Many(select="findImagesByIntroduceId"))
    })
    public List<Introduce> findIntroduceByUserId(int userId);
    
    /**
     * {@link UserKeyword} 조회
     */
    @Select("SELECT user_id as userId, content from user_keyword WHERE user_id = #{userId}")
    public List<UserKeyword> findKeywordsByUserId(int userId);

    /**
     * {@link Signature} 조회 
     */
    @Select("SELECT writer_id as writerId, content, write_time as writeTime from signature "
            + "WHERE user_id = #{userId} ORDER BY write_time DESC")
    public List<Signature> findSignaturesByUserId(int userId);

    /**
     * 소개글 이미지 조회
     */
    @Select("SELECT url from introduce_img WHERE introduce_id = #{introduceId}")
    public List<String> findImagesByIntroduceId(int introduceId);
}

