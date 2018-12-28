package com.gings.service;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;
import com.gings.domain.*;
import com.gings.model.DefaultRes;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;

import java.util.List;

public class BoardService {
    private final BoardMapper boardMapper;

    /**
     * 생성자 의존성 주입
     *
     * @param boardMapper
     */
    public BoardService(final BoardMapper boardMapper) { this.boardMapper = boardMapper; }


    /**
     * 전체 보드 조회
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes findAllBoard() {
        final List<Board> boards = boardMapper.findAllBoard();
        if (boards == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, boards);
    }

    /**
     * 보드 고유 번호로 보드 이미지 조회
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes findImagesByBoardId(final int id) {
        final List<String> images = boardMapper.findImagesByBoardId(id);
        for(String image : images){
            if(image == null || image.isEmpty())
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        }
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, images);
    }

    /**
     * 보드 고유 번호로 보드 키워드 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findKeywordsByBoardId(final int id) {
        final List<BoardKeyword> keywords = boardMapper.findKeywordsByBoardId(id);
            if(keywords == null)
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, keywords);
    }

    /**
     * 보드 고유 번호로 보드 추천 갯수 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes countRecommendByBoardId(final int id) {
        final int recommend = boardMapper.countRecommendByBoardId(id);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, recommend);
    }

    /**
     * 회원 고유 번호로 보드 조회
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findBoardByUserId(final int id) {
        final List<Board> boards = boardMapper.findBoardByUserId(id);
        if(boards == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, boards);
    }

    /**
     * 보드 고유 번호로 보드 댓글 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findReplyByBoardId(final int id) {
        final List<BoardReply> boardReplies = boardMapper.findReplyByBoardId(id);
        if(boardReplies == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, boardReplies);
    }

    /**
     * 보드 댓글 고유 번호로 보드 댓글 좋아요 갯수 조회
     *
     * @param id 보드 댓글 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findReplyRecommendNumbers(final int id) {
        final int replyRecommend = boardMapper.findReplyRecommendNumbers(id);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, replyRecommend);
    }

}
