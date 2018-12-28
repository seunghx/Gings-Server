package com.gings.dao;

import com.gings.domain.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 보드 전체 조회 (findAllBoard)
    @Select("SELECT * FROM board ORDER BY write_time DESC")
    @Results(value= {
            @Result(property="boardId", column="board_id"),
            @Result(property="writerId", column="writer_id"),
            @Result(property="title", column="title"),  @Result(property="content", column="content"),
            @Result(property="share", column="share_cnt"), @Result(property="time", column="write_time"),
            @Result(property="category", column="category"),
            @Result(property="images", column="board_id", javaType= List.class,
                    many=@Many(select="findImagesByBoardId")),
            @Result(property = "keywords", column = "board_id", javaType = List.class,
                    many=@Many(select="findKeywordsByBoardId")),
            @Result(property = "replys", column = "board_id", javaType = List.class,
                    many=@Many(select="findReplyByBoardId")),
            @Result(property = "recommender", column = "board_id", javaType = int.class,
                    one = @One(select = "countRecommendByBoardId"))
    })
    public List<Board> findAllBoard();

    // 보드 고유 번호로 이미지 전체 조회(findImagesByBoard)

    @Select("SELECT url FROM board_img WHERE board_id = #{boardId}")
    public List<String> findImagesByBoardId(int boardId);

    // 보드 고유 번호로 키워드 전체 조회(findKeywordsByBoardId)

    @Select("SELECT content FROM board_keyword WHERE board_id = #{boardId}")
    public List<BoardKeyword> findKeywordsByBoardId(int boardId);

    // 보드 고유 번호로 보드 좋아요 갯수 조회

    @Select("SELECT COUNT(recommender_id) FROM board_recommend WHERE board_id = #{boardId}")
    public int countRecommendByBoardId(int boardId);




    // 보드 고유 번호로 보드 조회
    @Select("SELECT * FROM board WHERE board_id = #{boardId}")
    @Results(value = {
            @Result(property="boardId", column="board_id"),
            @Result(property="writerId", column="writer_id"),
            @Result(property="title", column="title"),  @Result(property="content", column="content"),
            @Result(property="share", column="share_cnt"), @Result(property="time", column="write_time"),
            @Result(property="category", column="category"),
            @Result(property="images", column="board_id", javaType= List.class,
                    many=@Many(select="findImagesByBoardId")),
            @Result(property = "keywords", column = "board_id", javaType = List.class,
                    many=@Many(select="findKeywordsByBoardId")),
            @Result(property = "replys", column = "board_id", javaType = List.class,
                    many=@Many(select="findReplyByBoardId")),
            @Result(property = "recommender", column = "board_id", javaType = int.class,
                    one = @One(select = "countRecommendByBoardId"))
    })
    public List<Board> findBoardByUserId(int boardId);


    // 보드 고유 번호로 해당 보드 댓글 조회
    @Select("SELECT * FROM board_reply WHERE board_id = #{boardId} ORDER BY write_time DESC")
    @Results(value = {
            @Result(property = "recommender", column = "reply_id", javaType = int.class,
                    one = @One(select = "findReplyRecommendNumbers")),
            @Result(property = "writerId", column = "writer_id"),
            @Result(property = "content", column = "content"),
            @Result(property = "writeTime", column = "write_time")
    })
    public List<BoardReply> findReplyByBoardId(int boardId);


    // 댓글 고유 번호로 댓글 좋아요 갯수 조회

    @Select("SELECT COUNT(recommender_id) AS recommender FROM reply_recommend WHERE reply_id = #{replyId}")
    public int findReplyRecommendNumbers(int replyId);
}
