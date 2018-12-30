package com.gings.dao;

import com.gings.domain.*;
import com.gings.model.Pagination;
import com.gings.model.ReBoard;
import com.gings.model.UpBoard;
import org.apache.ibatis.annotations.*;
import org.elasticsearch.cluster.routing.allocation.decider.EnableAllocationDecider;


import java.util.List;

@Mapper
public interface BoardMapper {

    // 보드 전체 조회 (findAllBoard)
    @Select("SELECT * FROM board ORDER BY write_time DESC LIMIT #{pagination.limit} OFFSET #{pagination.offset}")
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
    public List<Board> findAllBoard(@Param("pagination") final Pagination pagination);

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
    public List<Board> findBoardByBoardId(int boardId);


    // 보드 고유 번호로 해당 보드 댓글 조회
    @Select("SELECT * FROM board_reply WHERE board_id = #{boardId} ORDER BY write_time DESC")
    @Results(value = {
            @Result(property = "recommender", column = "reply_id", javaType = int.class,
                    one = @One(select = "findReplyRecommendNumbersByReplyId")),
            @Result(property = "writerId", column = "writer_id"),
            @Result(property = "content", column = "content"),
            @Result(property = "writeTime", column = "write_time")
    })
    public List<BoardReply> findReplyByBoardId(int boardId);


    // 댓글 고유 번호로 댓글 좋아요 갯수 조회
    @Select("SELECT COUNT(recommender_id) AS recommender FROM reply_recommend WHERE reply_id = #{replyId}")
    public int findReplyRecommendNumbersByReplyId(int replyId);

    public void save(@Param("contentReq") final UpBoard.UpBoardReq upBoardReq);




     /*
    INSERT(CREATE) 하기

    UpBoard

     */

    //업보드 작성자, 제목, 내용, 카테고리 저장
    @Insert("INSERT INTO board(writer_id, title, content, category) VALUES(#{BoardReq.writerId}, " +
            "#{BoardReq.title}, #{BoardReq.content}, #{BoardReq.category})")
    @Options(useGeneratedKeys = true, keyProperty = "UpBoard.UpBoardReq.boardId")
    void saveBoard(@Param("BoardReq") final UpBoard.UpBoardReq boardReq);


    //업보드 사진 저장
    @Insert({"<script>", "insert into board_img(board_id, url) values ", "<foreach collection='images' " +
            "item='item' index='index' separator=', '>(#{boardId}, #{item})</foreach>","</script>"})
    void saveBoardImg(@Param("boardId") int boardId, @Param("img") List<String>images);


    //업보드 키워드 저장
    @Insert({"<script>", "insert into board_keyword(board_id, content) values ", "<foreach collection='keywords' " +
            "item='item' index='index' separator=', '>(#{boardId}, #{item})</foreach>","</script>"})
    void saveBoardKeyword(@Param("boardId") int boardId, @Param("keywords") List<String>keywords);


    // 업보드 좋아요한 사람 저장
    @Insert("INSERT INTO board_recommend(board_id, recommender_id) VALUES(#{UpBoardReq.boardId}, #{UpBoardReq.recommender})")
    void saveBoardRecommender(@Param("UpBoardReq") final UpBoard.UpBoardReq boardReq);


    /*

    ReBoard

     */
    //리보드 저장
    @Insert("INSERT INTO board_reply(board_id, writer_id, content) VALUES(#{ReBoardReq.boardId}, #{ReBoardReq.writerId}," +
            "#{ReBoardReq.content})")
    void saveReBoard(@Param("ReBoardReq") final ReBoard.ReBoardReq reBoardReq);

    //리보드 사진 저장
    @Insert({"<script>", "insert into reply_img(reply_id, url) values ", "<foreach collection='images' " +
            "item='item' index='index' separator=', '>(#{replyId}, #{item})</foreach>","</script>"})
    void saveReBoardImg(@Param("boardId") int boardId, @Param("img") List<String>images);

    // 리보드 좋아요한 사람 저장
    @Insert("INSERT INTO reply_recommend(reply_id, recommender_id) VALUES(#{ReBoardReq.boardId}, #{ReBoardReq.recommender})")
    void saveReBoardRecommender(@Param("ReBoardReq") final ReBoard.ReBoardReq reBoardReq);




    /*
    UPDATE 하기

    UpBoard
     */

    //업보드 수정하기
    @Update("UPDATE board SET title=#{BoardReq.title}, content=#{BoardReq.content} WHERE board_id = #{boardId}")
    void updateBoard(@Param("boardId") final int boardId, @Param("BoardReq") final UpBoard.UpBoardReq boardReq);

    //업보드 공유 갯수 업데이트
    @Update("UPDATE board SET share_cnt=#{UpBoardReq.share} WHERE board_id=#{boardId}")
    void updateBoardShare(@Param("boardId") final int boardId, @Param("UpBoardReq") final UpBoard.UpBoardReq upBoardReq);


    /*
    ReBoard
     */

    @Update("UPDATE board_reply SET content=#{ReBoardReq.content} WHERE reply_id = #{replyId}")
    void updateReBoard(@Param("replyId") final int replyId, @Param("ReBoardReq") final ReBoard.ReBoardReq reBoardReq);





    /*
    DELETE 하기
     */
    //보드 고유번호로 보드 삭제하기
    @Delete("DELETE FROM board WHERE board_id = #{boardId}")
    void deleteBoard(@Param("boardId") final int boardId);
}
