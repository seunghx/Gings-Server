package com.gings.dao;

import com.gings.domain.board.BoardReply;
import com.gings.model.MyPageBoard;
import com.gings.model.board.HomeBoard.HomeBoardAllRes;
import com.gings.model.board.HomeBoard.HomeBoardOneRes;
import com.gings.model.board.ModifyBoard.ModifyBoardReq;
import com.gings.model.Pagination;
import com.gings.model.board.ReBoard.ModifyReBoardReq;
import com.gings.model.board.ReBoard.ReBoardReq;
import com.gings.model.board.UpBoard.UpBoardReq;
import com.gings.utils.code.BoardCategory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 보드 전체 조회 (findAllBoard)
    @Select("SELECT * FROM board " +
            "ORDER BY write_time DESC LIMIT #{pagination.limit} OFFSET #{pagination.offset}")
    @Results(value= {
            @Result(property="boardId", column="board_id", id=true),
            @Result(property="writerId", column="writer_id"),
            @Result(property="title", column="title"),  @Result(property="content", column="content"),
            @Result(property="share", column="share_cnt"), @Result(property="time", column="write_time"),
            @Result(property="category", column="category"),
            @Result(property="images", column="board_id", javaType= List.class,
                    many=@Many(select="findImagesByBoardId")),
            @Result(property = "keywords", column = "board_id", javaType = List.class,
                    many=@Many(select="findKeywordsByBoardId")),
            @Result(property = "numOfReply", column = "board_id", javaType = int.class,
                    one=@One(select="countReply")),
            @Result(property = "recommender", column = "board_id", javaType = int.class,
                    one = @One(select = "countRecommendByBoardId")),
    })
    public List<HomeBoardAllRes> findAllBoard(@Param("pagination") final Pagination pagination);

    // 카테고리로 보드 전체 조회
    @Select("SELECT * FROM board WHERE category = #{category} " +
            "ORDER BY write_time DESC LIMIT #{pagination.limit} OFFSET #{pagination.offset}")
    @Results(value= {
            @Result(property="boardId", column="board_id", id=true),
            @Result(property="writerId", column="writer_id"),
            @Result(property="title", column="title"),  @Result(property="content", column="content"),
            @Result(property="share", column="share_cnt"), @Result(property="time", column="write_time"),
            @Result(property="category", column="category"),
            @Result(property="images", column="board_id", javaType= List.class,
                    many=@Many(select="findImagesByBoardId")),
            @Result(property = "keywords", column = "board_id", javaType = List.class,
                    many=@Many(select="findKeywordsByBoardId")),
            @Result(property = "numOfReply", column = "board_id", javaType = int.class,
                    one=@One(select="countReply")),
            @Result(property = "recommender", column = "board_id", javaType = int.class,
                    one = @One(select = "countRecommendByBoardId"))
    })
    public List<HomeBoardAllRes> findBoardsByCategory(@Param("category") final BoardCategory category,
                                                      @Param("pagination") final Pagination pagination);

    // 키워드로 보드 전체 조회(최신순)
    @Select("SELECT * FROM board LEFT JOIN board_keyword ON board.board_id = board_keyword.board_id " +
            "WHERE(title LIKE CONCAT('%',#{keyword},'%') "+
            "OR board.content LIKE CONCAT('%',#{keyword},'%') " +
            "OR board_keyword.content LIKE CONCAT('%',#{keyword},'%')) " +
            "ORDER BY write_time DESC LIMIT #{pagination.limit} OFFSET #{pagination.offset}")

    @Results(value= {
            @Result(property="boardId", column="board_id", id=true), @Result(property="writerId", column="writer_id"),
            @Result(property="title", column="title"),  @Result(property="content", column="content"),
            @Result(property="share", column="share_cnt"), @Result(property="time", column="write_time"),
            @Result(property="category", column="category"),
            @Result(property="images", column="board_id", javaType= List.class,
                    many=@Many(select="findImagesByBoardId")),
            @Result(property = "keywords", column = "board_id", javaType = List.class,
                    many=@Many(select="findKeywordsByBoardId")),
            @Result(property = "numOfReply", column = "board_id", javaType = int.class,
                    one=@One(select="countReply")),
            @Result(property = "recommender", column = "board_id", javaType = int.class,
                    one = @One(select = "countRecommendByBoardId")),
    })
    public List<HomeBoardAllRes> findBoardsByKeywordOrderByWriteTime(@Param("keyword") String keyword, @Param("pagination") Pagination pagination);

    // 키워드로 보드 전체 조회(추천순)
    @Select("SELECT * FROM board LEFT JOIN board_keyword ON board.board_id = board_keyword.board_id " +
            "WHERE(title LIKE CONCAT('%',#{keyword},'%') "+
            "OR board.content LIKE CONCAT('%',#{keyword},'%') " +
            "OR board_keyword.content LIKE CONCAT('%',#{keyword},'%')) " +
            "ORDER BY write_time DESC LIMIT #{pagination.limit} OFFSET #{pagination.offset}")

    @Results(value= {
            @Result(property="boardId", column="board_id", id=true), @Result(property="writerId", column="writer_id"),
            @Result(property="title", column="title"),  @Result(property="content", column="content"),
            @Result(property="share", column="share_cnt"), @Result(property="time", column="write_time"),
            @Result(property="category", column="category"),
            @Result(property="images", column="board_id", javaType= List.class,
                    many=@Many(select="findImagesByBoardId")),
            @Result(property = "keywords", column = "board_id", javaType = List.class,
                    many=@Many(select="findKeywordsByBoardId")),
            @Result(property = "numOfReply", column = "board_id", javaType = int.class,
                    one=@One(select="countReply")),
            @Result(property = "recommender", column = "board_id", javaType = int.class,
                    one = @One(select = "countRecommendByBoardId"))
    })
    public List<HomeBoardAllRes> findBoardsByKeywordOrderByRecommend(@Param("keyword")String keyword, @Param("pagination")Pagination pagination);

    // 카테고리별로 키워드로 보드 전체 조회(최신순)
    @Select("SELECT * FROM board LEFT JOIN board_keyword ON board.board_id = board_keyword.board_id " +
            "WHERE +" +
            "category = #{category} AND " +
            "(title LIKE CONCAT('%',#{keyword},'%') "+
            "OR board.content LIKE CONCAT('%',#{keyword},'%') " +
            "OR board_keyword.content LIKE CONCAT('%',#{keyword},'%')) " +
            "ORDER BY write_time DESC LIMIT #{pagination.limit} OFFSET #{pagination.offset}")
    @Results(value= {
            @Result(property="boardId", column="board_id", id=true), @Result(property="writerId", column="writer_id"),
            @Result(property="title", column="title"),  @Result(property="content", column="content"),
            @Result(property="share", column="share_cnt"), @Result(property="time", column="write_time"),
            @Result(property="category", column="category"),
            @Result(property="images", column="board_id", javaType= List.class,
                    many=@Many(select="findImagesByBoardId")),
            @Result(property = "keywords", column = "board_id", javaType = List.class,
                    many=@Many(select="findKeywordsByBoardId")),
            @Result(property = "numOfReply", column = "board_id", javaType = int.class,
                    one=@One(select="countReply")),
            @Result(property = "recommender", column = "board_id", javaType = int.class,
                    one = @One(select = "countRecommendByBoardId")),
    })
    public List<HomeBoardAllRes> findBoardsByCategoryByKeywordOrderByWriteTime(@Param("keyword")String keyword,
                                                                               @Param("category")BoardCategory category,
                                                                               @Param("pagination")Pagination pagination);

    // 카테고리별로 키워드로 보드 전체 조회(추천순)
    @Select("SELECT * FROM board LEFT JOIN board_keyword ON board.board_id = board_keyword.board_id " +
            "WHERE category = #{category} " +
            "OR(title LIKE CONCAT('%',#{keyword},'%') "+
            "OR board.content LIKE CONCAT('%',#{keyword},'%') " +
            "OR board_keyword.content LIKE CONCAT('%',#{keyword},'%')) " +
            "ORDER BY write_time DESC LIMIT #{pagination.limit} OFFSET #{pagination.offset}")

    @Results(value= {
            @Result(property="boardId", column="board_id", id=true), @Result(property="writerId", column="writer_id"),
            @Result(property="title", column="title"),  @Result(property="content", column="content"),
            @Result(property="share", column="share_cnt"), @Result(property="time", column="write_time"),
            @Result(property="category", column="category"),
            @Result(property="images", column="board_id", javaType= List.class,
                    many=@Many(select="findImagesByBoardId")),
            @Result(property = "keywords", column = "board_id", javaType = List.class,
                    many=@Many(select="findKeywordsByBoardId")),
            @Result(property = "numOfReply", column = "board_id", javaType = int.class,
                    one=@One(select="countReply")),
            @Result(property = "recommender", column = "board_id", javaType = int.class,
                    one = @One(select = "countRecommendByBoardId")),
    })
    public List<HomeBoardAllRes> findBoardsByCategoryByKeywordOrderByRecommend(@Param("keyword") String keyword, @Param("category")BoardCategory category,
                                                                               @Param("pagination") Pagination pagination);



    // 보드 고유 번호로 이미지 전체 조회(findImagesByBoard)
    @Select("SELECT url FROM board_img WHERE board_id = #{boardId}")
    public List<String> findImagesByBoardId(int boardId);

    // 보드 고유 번호로 키워드 전체 조회(findKeywordsByBoardId)
    @Select("SELECT content FROM board_keyword WHERE board_id = #{boardId}")
    public List<String> findKeywordsByBoardId(int boardId);

    // 이미지 url로 이미지 조회
    @Select("SELECT url FROM board_img WHERE url = #{url}")
    public String findImageByImageUrl(String url);

    // 키워드로 키워드 조회
    @Select("SELECT content FROM board_keyword WHERE content = #{keyword}")
    public String findKeywordByKeyword(String keyword);

    // 보드 고유 번호로 보드 좋아요수 조회
    @Select("SELECT COUNT(recommender_id) FROM board_recommend WHERE board_id = #{boardId}")
    public int countRecommendByBoardId(int boardId);

    // 보드 고유 번호로 보드 좋아요한 회원 고유 번호 조회
    @Select("SELECT board_id FROM board_recommend WHERE recommender_id = #{recommenderId}")
    public List<Integer> findBoardIdByRecommenderId(int recommenderId);

    // 회원 고유 번호로 좋아요한 보드 조회
    @Select("SELECT board_id FROM board_recommend WHERE recommender_id = #{userId}")
    public List<Integer> findRecommendBoardsByUserId(int userId);

    // 회원 고유 번호로 좋아요한 리보드 조회
    @Select("SELECT reply_id FROM reply_recommend WHERE recommender_id = #{userId}")
    public List<Integer> findRecommendRepliesByUserId(int userId);

    // 회원 고유 번호로 차단한 보드 조회
    @Select("SELECT board_id FROM board_block WHERE block_user_id = #{userId}")
    public List<Integer> findBlockBoardsByUserId(int userId);


    // 회원 고유 번호로 블랙리스트에 추가한 회원 고유 번호 조회
    @Select("SELECT to_id FROM blacklist WHERE from_id = #{userId}")
    public List<Integer> findBlackListUsersByUserId(int userId);


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
            @Result(property = "numOfReply", column = "board_id", javaType = int.class,
                    one=@One(select="countReply")),
            @Result(property = "recommender", column = "board_id", javaType = int.class,
                    one = @One(select = "countRecommendByBoardId"))
    })
    public HomeBoardOneRes findBoardByBoardId(int boardId);

    // 회원 고유 번호로 보드 조회
    @Select("SELECT * FROM board WHERE writer_id = #{userId} ORDER BY write_time DESC")
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
    public List<MyPageBoard> findBoardByUserId(int userId);

    // 회원 고유 번호로 보드 고유 번호 조회
    @Select("SELECT board_id FROM board WHERE writer_id = #{userId}")
    public List<Integer> findBoardIdByUserId(int userId);

    // 보드 고유 번호로 해당 리보드 조회
    @Select("SELECT * FROM board_reply WHERE board_id = #{boardId} ORDER BY write_time DESC")
    @Results(value = {
            @Result(property="replyId", column="reply_id"),
            @Result(property = "recommender", column = "reply_id", javaType = int.class,
                    one = @One(select = "findReplyRecommendNumbersByReplyId")),
            @Result(property = "writerId", column = "writer_id"),
            @Result(property = "content", column = "content"),
            @Result(property = "writeTime", column = "write_time"),
            @Result(property="images", column="reply_id", javaType= List.class,
                    many=@Many(select="findReplyImagesByReplyId")),

    })
    public List<BoardReply> findReplyByBoardId(int boardId);

    // 리보드 고유 번호로 리보드 조회
    @Select("SELECT * FROM board_reply WHERE reply_id = #{replyId}")
    @Results(value = {
            @Result(property = "replyId", column = "reply_id"),
            @Result(property = "writerId", column = "writer_id"),
            @Result(property = "content", column = "content"),
            @Result(property = "writeTime", column = "write_time"),
            @Result(property="images", column="reply_id", javaType= List.class,
                    many=@Many(select="findReplyImagesByReplyId")),
    })

    public BoardReply findReplyByReplyId(int replyId);

    // 보드 고유 번호로 댓글수 조회
    @Select("SELECT COUNT(reply_id) FROM board_reply WHERE board_id = #{boardId}")
    public int countReply(int boardId);

    // 댓글 고유 번호로 댓글 이미지 전체 조회
    @Select("SELECT url FROM reply_img WHERE reply_id = #{replyId}")
    public List<String> findReplyImagesByReplyId(int replyId);


    // 리보드 고유 번호로 리보드 좋아요수 조회
    @Select("SELECT COUNT(recommender_id) AS recommender FROM reply_recommend WHERE reply_id = #{replyId}")
    public int findReplyRecommendNumbersByReplyId(int replyId);

    @Select("SELECT board_id FROM board_reply WHERE reply_id = #{replyId}")
    public int findBoardIdByReplyId(int replyId);

    // 회원 고유 번호로 좋아요 한 리보드 조회
    @Select("SELECT reply_id FROM reply_recommend WHERE recommender_id = #{userId}")
    public List<Integer> findRecommendReBoardsByUserId(int userId);




     /*
    INSERT(CREATE) 하기
    UpBoard
     */

    //업보드 작성자, 제목, 내용, 카테고리 저장
    @Insert("INSERT INTO board(writer_id, title, content, category) VALUES(#{boardReq.writerId}, " +
            "#{boardReq.title}, #{boardReq.content}, #{boardReq.category})")
    @Options(useGeneratedKeys = true, keyProperty = "boardReq.boardId", keyColumn="board_id")
    void saveBoard(@Param("boardReq") final UpBoardReq boardReq);


    //업보드 사진 저장
    @Insert({"<script>", "insert into board_img(board_id, url) values ", "<foreach collection='images' " +
            "item='item' index='index' separator=', '>(#{boardId}, #{item})</foreach>","</script>"})

    void saveBoardImg(@Param("boardId") int boardId, @Param("images") List<String>images);


    //업보드 키워드 저장
    @Insert({"<script>", "insert into board_keyword(board_id, content) values ", "<foreach collection='keywords' " +
            "item='item' index='index' separator=', '>(#{boardId}, #{item})</foreach>","</script>"})
    void saveBoardKeyword(@Param("boardId") int boardId, @Param("keywords") List<String>keywords);


    // 업보드 좋아요한 사람 저장
    @Insert("INSERT INTO board_recommend(board_id, recommender_id) VALUES(#{boardId}, #{userId})")
    void saveBoardRecommender(@Param("boardId") int boardId, @Param("userId") int userId);

    // 업보드 차단한 사람 저장
    @Insert("INSERT INTO board_block(board_id, block_user_id) VALUES(#{boardId}, #{userId})")
    void saveBoardBlockUser(@Param("boardId") int boardId, @Param("userId") int userId);

    // 업보드 차단한 사람 저장
    @Insert("INSERT INTO blacklist(from_id, to_id) VALUES(#{userId}, #{blockUserId})")
    void saveBlackListUser(@Param("userId") int userId, @Param("blockUserId") int blockUserId);


    /*
    ReBoard
     */
    //리보드 저장
    @Insert("INSERT INTO board_reply(board_id, writer_id, content) VALUES(#{reBoardReq.boardId}, #{reBoardReq.writerId}," +
            "#{reBoardReq.content})")
    @Options(useGeneratedKeys = true, keyProperty = "reBoardReq.replyId", keyColumn="reply_id")
    void saveReBoard(@Param("reBoardReq") final ReBoardReq reBoardReq);

    //리보드 사진 저장
    @Insert({"<script>", "insert into reply_img(reply_id, url) values ", "<foreach collection='images' " +
            "item='item' index='index' separator=', '>(#{replyId}, #{item})</foreach>","</script>"})
    void saveReBoardImg(@Param("replyId") int replyId, @Param("images") List<String>images);

    // 리보드 좋아요한 사람 저장
    @Insert("INSERT INTO reply_recommend(reply_id, recommender_id) VALUES(#{replyId}, #{userId})")
    void saveReBoardRecommender(@Param("replyId") int replyId, @Param("userId") int userId);




    /*
    UPDATE 하기
    UpBoard
     */

    //업보드 수정
    @Update("UPDATE board SET title=#{ModifyBoardReq.title}, content=#{ModifyBoardReq.content}, category=#{ModifyBoardReq.category}, write_time = now() WHERE board_id = #{boardId}")
    void updateBoard(@Param("boardId") final int boardId, @Param("ModifyBoardReq") final ModifyBoardReq modifyBoardReq);

    //업보드 공유 갯수 업데이트
    @Update("UPDATE board SET share_cnt = share_cnt + 1 WHERE board_id=#{boardId}")
    void updateBoardShare(@Param("boardId") final int boardId);


    /*
    ReBoard
     */
    // 리보드 수정
    @Update("UPDATE board_reply SET content=#{ModifyReBoardReq.content}, write_time = now() WHERE reply_id = #{replyId}")
    void updateReBoard(@Param("replyId") final int replyId, @Param("ModifyReBoardReq") final ModifyReBoardReq modifyReBoardReq);


    /*
    DELETE 하기
     */
    //보드 고유번호로 보드 삭제하기
    @Delete("DELETE FROM board WHERE board_id = #{boardId}")
    void deleteBoard(@Param("boardId") final int boardId);

    //회원 고유번호로 보드 삭제하기
    @Delete("DELETE FROM board WHERE user_id = #{userId}")
    void deleteBoardByUserId(@Param("userId") final int userId);

    //리보드 고유번호로 리보드 삭제하기
    @Delete("DELETE FROM board_reply WHERE reply_id = #{reboardId}")
    void deleteReBoard(@Param("reboardId") final int reboardId);

    //회원 고유번호로 리보드 삭제하기
    @Delete("DELETE FROM board_reply WHERE user_id = #{userId}")
    void deleteReBoardByUserId(@Param("userId") final int userId);



    //보드 이미지 고유 번호로 보드 이미지 삭제하기
    @Delete("DELETE FROM board_img WHERE url = #{imageUrl}")
    void deleteBoardImg(@Param("imageUrl") final String imageUrl);

    //보드 이미지 고유 번호로 보드 이미지 삭제하기
    @Delete("DELETE FROM reply_img WHERE url = #{imageUrl}")
    void deleteReBoardImg(@Param("imageUrl") final String imageUrl);

    //보드 고유번호로 보드 키워드 삭제하기
    @Delete("DELETE FROM board_keyword WHERE content = #{keyword}")
    void deleteBoardKeyword(@Param("keyword") final String keyword);

    //회원 고유 번호와 보드 고유 번호로 추천 취소하기
    @Delete("DELETE FROM board_recommend WHERE board_id = #{boardId} AND recommender_id = #{userId}")
    void deleteBoardRecommender(@Param("boardId") final int boardId, @Param("userId") final int userId);

    //회원 고유 번호와 리보드 고유 번호로 추천 취소하기
    @Delete("DELETE FROM reply_recommend WHERE reply_id = #{replyId} AND recommender_id = #{userId}")
    void deleteReBoardRecommender(@Param("replyId") final int replyId, @Param("userId") final int userId);

    //회원 고유 번호와 보드 고유 번호로 차단 취소하기
    @Delete("DELETE FROM board_block WHERE board_id = #{boardId} AND block_user_id = #{userId}")
    void deleteBoardBlockUser(@Param("boardId") final int boardId, @Param("userId") final int userId);
}