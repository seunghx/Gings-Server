package com.gings.dao;


import com.gings.controller.LoginController.LoginUser;

import com.gings.domain.*;
import com.gings.model.GuestModel;
import com.gings.model.IntroduceModel;
import com.gings.model.MyPage;
import com.gings.model.Pagination;
import org.apache.ibatis.annotations.*;

import java.util.List;

import org.apache.ibatis.mapping.FetchType;

import com.gings.model.user.SignUp;
import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 *
 * @author seunghyun
 *
 */
@Mapper
public interface UserMapper {

    /**
     * {@link User} 조회
     */
    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    @Results(value = {
            @Result(property = "id", column = "user_id"), @Result(property = "email", column = "email"),
            @Result(property = "name", column = "name"), @Result(property = "pwd", column = "pwd"),
            @Result(property = "region", column = "region"), @Result(property = "job", column = "job"),
            @Result(property = "company", column = "company"), @Result(property = "field", column = "field"),
            @Result(property = "status", column = "status"), @Result(property = "role", column = "role"),
            @Result(property = "image", column = "image"),
            @Result(property = "coworkingEnabled", column = "coworking_chk"),
            @Result(property = "introduce", column = "user_id", javaType = List.class,
                    many = @Many(select = "findIntroduceByUserId")),
            @Result(property = "keywords", column = "user_id", javaType = List.class,
                    many = @Many(select = "findKeywordsByUserId")),
            @Result(property = "signatures", column = "user_id", javaType = List.class,
                    many = @Many(select = "findSignaturesByUserId", fetchType = FetchType.LAZY))
    })
    public User findByUserId(int userId);


    @Select("SELECT user_id as userId, pwd, role, first_login as firstLogin"
            + " FROM user "
            + " WHERE email = #{email}")
    public LoginUser findByEmail(@Param("email") String email);

    /**
     * {@link User} 조회
     */
    @Select("SELECT * FROM user WHERE name LIKE CONCAT('%',#{keyword},'%') OR company LIKE CONCAT('%',#{keyword},'%') OR field LIKE CONCAT('%',#{keyword},'%') OR job LIKE CONCAT('%',#{keyword},'%') ")
    @Results(value = {
            @Result(property = "id", column = "user_id"), @Result(property = "name", column = "name"),
            @Result(property = "company", column = "company"), @Result(property = "job", column = "job"),
            @Result(property = "field", column = "field"), @Result(property = "coworkingChk", column = "coworking_chk"),
            @Result(property = "image", column = "image"),
            @Result(property = "introduce", column = "user_id", javaType = List.class,
                    many = @Many(select = "findIntroduceByUserId"))
    })
    public List<Directory> findUsersByKeyword(String keyword);

    /**
     * {@link User} 조회
     */
    @Select("SELECT * FROM user LEFT JOIN introduce ON user.user_id = introduce.user_id ORDER BY write_time DESC LIMIT #{pagination.limit} OFFSET #{pagination.offset}")
    @Results(value = {
            @Result(property = "id", column = "user_id"), @Result(property = "name", column = "name"),
            @Result(property = "company", column = "company"), @Result(property = "job", column = "job"),
            @Result(property = "field", column = "field"), @Result(property = "coworkingChk", column = "coworking_chk"),
            @Result(property = "image", column = "image"), @Result(property = "writeTime", column = "write_time"),
            @Result(property = "introduce", column = "user_id", javaType = List.class,
                    many = @Many(select = "findIntroduceByUserId"))
    })
    public List<Directory> findUsersByWriteTime(@Param("pagination") final Pagination pagination);

    /**
     * {@link Introduce} 조회
     */
    @Select("SELECT * from introduce WHERE user_id = #{userId}")
    @Results({
            @Result(property = "id", column = "introduce_id"),
            @Result(property = "content", column = "content"),
            @Result(property = "imgs", column = "introduce_id", javaType = List.class,
                    many = @Many(select = "findImagesByIntroduceId"))
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

    @Select("SELECT COUNT(*) from user WHERE email = #{email}")
    public int countByEmail(String email);

    @Update("UPDATE user SET first_login = 0 WHERE user_id = #{userId}")
    public void setFalseToFirstLogin(int userId);

    //회원 등록(회원가입)
    @Insert("INSERT INTO user(name,email,pwd) "
            + "VALUES(#{signUp.name}, #{signUp.email}, #{signUp.pwd})")
    @Options(useGeneratedKeys = true, keyColumn = "user.userIdx")
    int save(@Param("signUp") final SignUp signUp);

    //회원 정보 수정
    @Update("UPDATE user SET region = #{user.region}, job = #{user.job}, company = #{user.company}, field = #{user.field}, coworking_chk = #{user.coworking}," +
            "status = #{user.status}, role = #{user.role}, image = #{user.image} WHERE userIdx = #{userIdx}")
    void update(@Param("userIdx") final int userIdx, @Param("user") final User user);


    //회원 삭제
    @Delete("DELETE FROM user WHERE user_id = #{userId}")
    void deleteUser(@Param("userId") final int userId);

    //================================================================================================================
    //=================================================================================================================

    /*
    회원 고유 번호로 유저 정보 조회
     */
    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    @Results(value = {
            @Result(property = "id", column = "user_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "region", column = "region"), @Result(property = "job", column = "job"),
            @Result(property = "company", column = "company"),@Result(property = "field", column = "field"),
            @Result(property = "status", column = "status"), @Result(property = "coworkingEnabled", column = "coworking_chk"),
            @Result(property = "image", column = "image"),
            @Result(property = "keywords", column = "user_id", javaType = List.class,
                    many = @Many(select = "findKeywordsByUserId2"))
    })
    public MyPage findByUserId2(int userId);

    @Select("SELECT content FROM user_keyword WHERE user_id = #{userId}")
    public List<String> findKeywordsByUserId2(int userId);


    //=================================================================================================================

    /*
    회원 고유 번호로 자기소개 글 조회
     */
    @Select("SELECT * FROM introduce WHERE user_id = #{userId}")
    @Results(value = {
            @Result(property = "content", column = "content"),
            @Result(property = "imgs", column = "introduce_id", javaType = List.class,
                    many = @Many(select = "findImagesByIntroduceId")),
            @Result(property = "time", column = "write_time")
    })
    public MyPage.MyPageIntro findUserIntro(int userId);


    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    @Results(value = {
            @Result(property = "id", column = "user_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "field", column = "field"),
            @Result(property = "status", column = "status"),
            @Result(property = "coworkingEnabled", column = "coworking_chk"),
            @Result(property = "image", column = "image")
    })
    public MyPage.MyPageOther findUserOtherInfoByUserId(int userId);

    /*
    회원 고유 번호로 게스트 보드 조회
     */

    @Select("SELECT * FROM guestboard WHERE user_id = #{userId} ORDER BY write_time DESC")
    @Results(value ={
            @Result(property = "guestModelUser", column = "writer_id", javaType = GuestModel.GuestModelUser.class,
                    one = @One(select = "findByUserId3")),
            @Result(property = "content", column = "content"),
            @Result(property = "time", column = "write_time")
    })
    public List<GuestModel.GuestModelRes> findGuestBoardByUserId(int userId);

    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    @Results(value = {
            @Result(property = "id", column = "user_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "job", column = "job"),
            @Result(property = "company", column = "company"),
            @Result(property = "image", column = "image")
    })
    public GuestModel.GuestModelUser findByUserId3(int userId);


    /*
    Guest Board 저장
    */
    @Insert("INSERT INTO guestboard(user_id, writer_id, content) VALUES(#{myPageUserId}, #{id}, #{guestModelReq.content})")
    void saveGuest(@Param("guestModelReq") final GuestModel.GuestModelReq guestModelReq, @Param("myPageUserId") final int myPageUserId, @Param("id") final int id);


    //==================================================================================================================

    /*
    자기소개 수정 전 select
     */
    @Select("SELECT * FROM introduce WHERE user_id = #{userId}")
    @Results(value = {
            @Result(property = "content", column = "content"),
            @Result(property = "imgs", column = "introduce_id", javaType = List.class,
                    many = @Many(select = "findImagesByIntroduceId"))
    })
    public List<IntroduceModel.IntroduceRes> selectIntroBeforeChange(int userId);

    /*
    자기소개 저장(최초)
     */
    @Insert("INSERT INTO introduce(user_id, content) VALUES(#{id}, #{introduceReq.content})")
    @Options(useGeneratedKeys = true, keyProperty = "introduceReq.id", keyColumn = "introduce_id")
    void saveIntroByUserId(@Param("id") final int id, @Param("introduceReq") final IntroduceModel.IntroduceReq introduceReq);

    @Insert({"<script>", "insert into introduce_img(introduce_id, url) values ", "<foreach collection='image' "+
            "item='item' index='index' separator=', ' > (#{introduceId}, #{item})</foreach>","</script>"})
    void saveIntroduceImg(@Param("introduceId") final int introduceId, @Param("image") List<String> image);

    /*
    자기소개 수정
     */
    @Update("UPDATE introduce SET content= #{introduceReq.content} WHERE user_id = #{id}")
    @Options(useGeneratedKeys = true, keyProperty = "introduceReq.id", keyColumn = "id")
    void updateIntroduce(@Param("id") final int id, @Param("introduceReq") final IntroduceModel.IntroduceReq introduceReq);


    @Delete("DELETE FROM introduce_img WHERE url= #{imgUrl}")
    void deleteIntroduceImg(@Param("imgUrl") final String imgUrl);


    @Insert({"<script>", "insert into introduce_img(introduce_id, url) values ", "<foreach collection='image' "+
            "item='item' index='index' separator=', ' > (#{introduceId}, #{item})</foreach>","</script>"})
    void updateIntroduceImg(@Param("introduceId") final int introduceId, @Param("image") List<String> image);


    /*
    프로필 사진 조회
     */
    @Select("SELECT image FROM user WHERE user_id = #{id}")
    public MyPage.MyPageProfile selectProfileImg(@Param("id") final int id);
    /*
    프로필 사진 저장
     */
    @Update("UPDATE user SET image= #{url} WHERE user_id = #{id}")
    void updateProfileImg(@Param("id") final int id, @Param("url") final String url);

    /*
    프로필 정보 수정
     */
    @Update("UPDATE user SET region=#{myPage.region}, job=#{myPage.job}, company=#{myPage.company}, field=#{myPage.field}, " +
            "coworking_chk=#{myPage.coworkingEnabled}, status=#{myPage.status} WHERE user_id=#{id}")
    @Options(useGeneratedKeys = true, keyProperty = "myPage.user_id", keyColumn = "id")
    void saveProfileInfo(@Param("id") final int id, @Param("myPage") final MyPage myPage);


    /*
    프로필 정보 키워드 수정
     */
    @Delete("DELETE FROM user_keyword WHERE user_id = #{id}")
    void deleteKeyword(@Param("id") final int id);

    @Insert({"<script>", "insert into user_keyword(user_id, content) values ", "<foreach collection='keyword' " +
            "item='item' index='index' separator=', '> (#{id}, #{item})</foreach>", "</script>"})
    void saveProfileKeyword(@Param("id") final int id, @Param("keyword") List<String> keyword);


    /*
    비밀번호 변경
     */
    @Update("UPDATE user SET pwd= #{pwd.pwd} WHERE user_id = #{id}")
    void updatePwd(@Param("id") final int id, @Param("pwd") final MyPage.MyPagePwdRes pwd);

    @Select("SELECT pwd FROM user WHERE user_id = #{id}")
    public String getPwdByUserId(@Param("id") final int id);


}
