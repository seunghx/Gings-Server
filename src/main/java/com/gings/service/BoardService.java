package com.gings.service;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;
import com.gings.domain.*;
import com.gings.model.DefaultRes;
import com.gings.model.board.HomeBoard.HomeBoardOneRes;
import com.gings.model.board.HomeBoard.HomeBoardAllRes;
import com.gings.model.board.ModifyBoard.ModifyBoardReq;
import com.gings.model.Pagination;
import com.gings.model.board.ReBoard.ReBoardReq;
import com.gings.model.board.UpBoard;
import com.gings.model.board.UpBoard.UpBoardOneRes;
import com.gings.model.board.UpBoard.UpBoardReq;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;


@Slf4j
@Service
public class BoardService {
    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);
    private final BoardMapper boardMapper;
    private final UserMapper userMapper;
    private final S3MultipartService s3MultipartService;

    /**
     * 생성자 의존성 주입
     *
     * @param boardMapper
     */
    public BoardService(final BoardMapper boardMapper, final UserMapper userMapper,
                        final S3MultipartService s3MultipartService) {
        this.boardMapper = boardMapper;
        this.userMapper = userMapper;
        this.s3MultipartService = s3MultipartService;
    }


    /**
     * 전체 보드 조회
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<HomeBoardAllRes>> findAllBoard(final Pagination pagination) {
        final List<HomeBoardAllRes> boards = boardMapper.findAllBoard(pagination);
        for(HomeBoardAllRes board : boards) board.setWriter(userMapper.findByUserId(board.getWriterId()).getName());
        if (boards.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_ALL_BOARDS, boards);
    }

    /**
     * 보드 고유 번호로 보드 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<HomeBoardOneRes> findBoardByBoardId(final int id) {
        final HomeBoardOneRes board = boardMapper.findBoardByBoardId(id);
        board.setWriter(userMapper.findByUserId(board.getWriterId()).getName());
        if (board == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD, board);
    }

    /**
     * 보드 고유 번호로 보드 이미지 조회
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<String>> findImagesByBoardId(final int id) {
        final List<String> images = boardMapper.findImagesByBoardId(id);
        if (images.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD_INFO);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, images);
    }

    /**
     * 보드 고유 번호로 보드 키워드 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<List<String>> findKeywordsByBoardId(final int id) {
        final List<String> keywords = boardMapper.findKeywordsByBoardId(id);
        if (keywords.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD_INFO);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, keywords);
    }

    /**
     * 회원 고유 번호로 보드 찾기
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<List<Board>> findBoardByUserId(final int id) {
        final List<Board> boards = boardMapper.findBoardByUserId(id);
        if (boards==null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD, boards);
    }
    public DefaultRes<List<Board>>checkBoardByUser(final int id) {
        final List<Board> boards = boardMapper.findBoardByUserId(id);
        if (boards==null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.UNQUALIFIED, boards);
    }

    /**
     * 보드 고유 번호로 보드 추천수 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<Integer> countRecommendByBoardId(final int id) {
        final int recommend = boardMapper.countRecommendByBoardId(id);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, recommend);
    }

    /**
     * 보드 고유 번호로 보드 댓글 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<List<BoardReply>> findReplyByBoardId(final int id) {
        final List<BoardReply> boardReplies = boardMapper.findReplyByBoardId(id);
        if (boardReplies.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD_INFO);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, boardReplies);
    }

    /**
     * 보드 댓글 고유 번호로 보드 댓글 좋아요수 조회
     *
     * @param id 보드 댓글 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findReplyRecommendNumbersByReplyId(final int id) {
        final int replyRecommend = boardMapper.findReplyRecommendNumbersByReplyId(id);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, replyRecommend);
    }


    /**
     * 보드 저장
     *
     * @param upBoardReq 보드 데이터
     * @return DefaultRes
     */
    public DefaultRes saveBoard(final UpBoardReq upBoardReq) {
        try {
            boardMapper.saveBoard(upBoardReq);
            final int boardId = upBoardReq.getBoardId();

            List<String> urlList = s3MultipartService.uploadMultipleFiles(upBoardReq.getImages());
            boardMapper.saveBoardImg(boardId, urlList);

            boardMapper.saveBoardKeyword(boardId, upBoardReq.getKeywords());

            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATE_BOARD);
        } catch (Exception e) {
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 보드 좋아요 & 좋아요 취소
     *
     * @param boardId    보드
     * @param userId    회원 고유 번호
     * @return DefaultRes
     */

    public DefaultRes BoardLikes(final int boardId, final int userId) {
        try {
            if(boardMapper.findBoardByBoardId(boardId) == null)
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);

            List<Integer> boardIdList = boardMapper.findRecommendBoardsByUserId(userId);

            Board.BoardRecommend boardLike = new Board.BoardRecommend();

            for(int id : boardIdList) {
                if (id == boardId) {
                    boardMapper.deleteBoardRecommender(boardId, userId);
                    boardLike.setRecommendBoardIdList(boardMapper.findBoardIdByRecommenderId(userId));
                    return DefaultRes.res(StatusCode.OK, ResponseMessage.CANCEL_LIKE_BOARD, boardLike);
                }
            }

            boardMapper.saveBoardRecommender(boardId, userId);
            boardLike.setRecommendBoardIdList(boardMapper.findBoardIdByRecommenderId(userId));
            return DefaultRes.res(StatusCode.OK, ResponseMessage.LIKE_BOARD, boardLike);

        } catch (Exception e) {
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 리보드 저장
     *
     * @param reBoardReq 보드 데이터
     * @return DefaultRes
     */

    public DefaultRes saveReBoard(final ReBoardReq reBoardReq) {
        try {
            boardMapper.saveReBoard(reBoardReq);
            final int reReplyId = reBoardReq.getReplyId();

            List<String> urlList = s3MultipartService.uploadMultipleFiles(reBoardReq.getImages());
            boardMapper.saveReBoardImg(reReplyId, urlList);

            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATE_REBOARD);
        } catch (Exception e) {
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 리보드 좋아요 & 좋아요 취소
     *
     * @param replyId    보드
     * @param userId    회원 고유 번호
     * @return DefaultRes
     */

    public DefaultRes ReBoardLikes(final int replyId, final int userId) {
        try {
            List<Integer> reBoardIdList = boardMapper.findRecommendReBoardsByUserId(userId);
            for(int id : reBoardIdList){
                if(id == replyId){
                    boardMapper.deleteReBoardRecommender(replyId,userId);
                    return DefaultRes.res(StatusCode.OK, ResponseMessage.CANCEL_LIKE_REBOARD);
                }
            }
            boardMapper.saveReBoardRecommender(replyId, userId);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.LIKE_REBOARD);
        } catch (Exception e) {
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 보드 수정
     *
     * @param boardId 보드 데이터
     * @param modifyBoardReq 보드 데이터
     * @return DefaultRes
     */

    public DefaultRes updateBoard(final int boardId, final ModifyBoardReq modifyBoardReq){
        try {
            boardMapper.updateBoard(boardId, modifyBoardReq);

            s3MultipartService.deleteMultipleFiles(modifyBoardReq.getPrevImagesUrl());
            Boolean isImageDB = true;
            if(isImageDB) {
                for (String url : modifyBoardReq.getPrevImagesUrl()) {
                    if (boardMapper.findImageByImageUrl(url) == url) {
                        boardMapper.deleteBoardImg(url);
                    }
                }
            }
            else{
                return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATE_BOARD);
            }
            List<String> urlList = s3MultipartService.uploadMultipleFiles(modifyBoardReq.getPostImages());
            boardMapper.saveBoardImg(boardId, urlList);

            for(String keywords : modifyBoardReq.getPrevKeywords()){
                boardMapper.deleteBoardKeyword(keywords);
            }
            boardMapper.saveBoardKeyword(boardId, modifyBoardReq.getPostKeywords());

            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATE_BOARD);
        } catch (Exception e) {
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }
}

