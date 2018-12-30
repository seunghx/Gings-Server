package com.gings.service;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;
import com.gings.domain.*;
import com.gings.model.DefaultRes;
import com.gings.model.Pagination;
import com.gings.model.UpBoard;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class BoardService {
    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);
    private final BoardMapper boardMapper;

    /**
     * 생성자 의존성 주입
     *
     * @param boardMapper
     */
    public BoardService(final BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }


    /**
     * 전체 보드 조회
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<Board>> findAllBoard(final Pagination pagination) {
        final List<Board> boards = boardMapper.findAllBoard(pagination);
        if (boards.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD, boards);
    }


    /**
     * 보드 고유 번호로 보드 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findBoardByBoardId(final int id) {
        final List<Board> boards = boardMapper.findBoardByBoardId(id);
        if (boards.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD, boards);
    }

    /**
     * 보드 고유 번호로 보드 이미지 조회
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes findImagesByBoardId(final int id) {
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
    public DefaultRes findKeywordsByBoardId(final int id) {
        final List<BoardKeyword> keywords = boardMapper.findKeywordsByBoardId(id);
        if (keywords.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD_INFO);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, keywords);
    }

    /**
     * 보드 고유 번호로 보드 추천 갯수 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes countRecommendByBoardId(final int id) {
        final int recommend = boardMapper.countRecommendByBoardId(id);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, recommend);
    }

    /**
     * 보드 고유 번호로 보드 댓글 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findReplyByBoardId(final int id) {
        final List<BoardReply> boardReplies = boardMapper.findReplyByBoardId(id);
        if (boardReplies.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD_INFO);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, boardReplies);
    }

    /**
     * 보드 댓글 고유 번호로 보드 댓글 좋아요 갯수 조회
     *
     * @param id 보드 댓글 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findReplyRecommendNumbersByReplyId(final int id) {
        final int replyRecommend = boardMapper.findReplyRecommendNumbersByReplyId(id);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, replyRecommend);
    }

    /**
     * 보드 작성
     *
     * @param upBoardReq 보드 데이터
     * @return DefaultRes
     */
    public DefaultRes save(final UpBoard.UpBoardReq upBoardReq) {
        try {
            boardMapper.save(upBoardReq);
            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATE_BOARD);
        } catch (Exception e) {
            log.info(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 컨텐츠 좋아요 & 좋아요 취소
     *
     * @param userId    서비스 요청한 사람의 고유 번호
     * @param boardId 컨텐츠 고유 번호
     * @return DefaultRes
     */
/*
    public DefaultRes likes(final int userId, final int boardId) {
        final int recommend = boardMapper.countRecommendByBoardId(boardId);
        try {
            if (recommend > -1) {
                //좋아요 카운트 반영
                contentMapper.like(contentIdx, content.getLikeCount() + 1);
                //좋아요
                contentLikeMapper.save(userIdx, contentIdx);
            } else {
                //싫어요 카운트 반영
                contentMapper.like(contentIdx, content.getLikeCount() - 1);
                //싫어요
                contentLikeMapper.deleteByUserIdxAndContentIdx(userIdx, contentIdx);
            }

            content = findByContentIdx(contentIdx).getData();
            content.setAuth(checkAuth(userIdx, contentIdx));
            content.setLike(checkLike(userIdx, contentIdx));

            return DefaultRes.res(StatusCode.OK, ResponseMessage.LIKE_CONTENT, content);
        } catch (Exception e) {
            log.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }
    */


}

