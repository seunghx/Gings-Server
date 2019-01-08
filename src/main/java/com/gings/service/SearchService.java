package com.gings.service;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;
import com.gings.domain.Board;
import com.gings.domain.Directory;
import com.gings.domain.User;
import com.gings.model.DefaultRes;
import com.gings.model.Pagination;
import com.gings.model.SearchKeyword.SearchKeywordReq;
import com.gings.model.board.HomeBoard;
import com.gings.model.board.HomeBoard.HomeBoardAllRes;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import com.gings.utils.code.BoardCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class SearchService {

    private final UserMapper userMapper;
    private final BoardMapper boardMapper;
    private final BoardService boardService;

    /**
     * 생성자 의존성 주입
     *
     * @param userMapper
     */
    public SearchService(final UserMapper userMapper, final BoardMapper boardMapper,
                         final BoardService boardService){
        this.userMapper = userMapper;
        this.boardMapper = boardMapper;
        this.boardService = boardService;
    }

    /**
     * 디렉토리 검색
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<Directory>> selectUserByKeyword(final String keyword, final Pagination pagination) {
        final List<Directory> users = userMapper.findUsersByKeyword(keyword);
        if (users.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_SEARCH_RESULT);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.SEARCH_DIRECTORY, users);
    }

    /**
     * 최신순 디렉토리 조회
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<Directory>> selectUserByWriteTime(final Pagination pagination) {
        final List<Directory> users = userMapper.findUsersByWriteTime(pagination);
        if (users.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_INTRODUCE);

        boolean deletedChk = false;
        while(true) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getIntroduce().isEmpty()) {
                    users.remove(i);
                    deletedChk = true;
                }
            }
            if (deletedChk == false) break;
            else deletedChk = false;
        }

        return DefaultRes.res(StatusCode.OK, ResponseMessage.YES_INTRODUCE, users);
    }

    /**
     * 보드 검색(최신순)
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<HomeBoardAllRes>> selectBoardByKeywordByWriteTime(final String keyword, final Pagination pagination, final int userId) {
        List<HomeBoardAllRes> boards =
                boardService.setUserInfoInAllRes(boardMapper.findBoardsByKeywordOrderByWriteTime(keyword, pagination),userId);
        boards = deleteOverlapBoard(boards);
        if (boards.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_SEARCH_RESULT);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.SEARCH_BOARD, boards);
    }

    /**
     * 보드 검색(추천순)
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<HomeBoardAllRes>> selectBoardByKeywordByRecommend(final String keyword, final Pagination pagination,
                                                                             final int userId) {
        List<HomeBoardAllRes> boards =
                boardService.setUserInfoInAllRes(boardMapper.findBoardsByKeywordOrderByRecommend(keyword, pagination),userId);
        boards = deleteOverlapBoard(boards);
        if (boards.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_SEARCH_RESULT);

        Collections.sort(boards);

        return DefaultRes.res(StatusCode.OK, ResponseMessage.SEARCH_BOARD, boards);
    }

    /**
         * 카테고리별 보드 검색(최신순)
         *
         * @param
         * @return DefaultRes
         */
        public DefaultRes<List<HomeBoardAllRes>> selectBoardByCategoryByKeywordByWriteTime(final String keyword, final BoardCategory boardCategory,
                                                                                           final Pagination pagination, final int userId) {
            List<HomeBoardAllRes> boards =
                    boardService.setUserInfoInAllRes(boardMapper.findBoardsByCategoryByKeywordOrderByWriteTime(keyword, boardCategory, pagination), userId);
            boards = deleteOverlapBoard(boards);
            if (boards.isEmpty())
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_SEARCH_RESULT);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.SEARCH_BOARD, boards);
    }

    /**
     * 카테고리별 보드 검색(추천순)
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<HomeBoardAllRes>> selectBoardByCategoryByKeywordByRecommend(final String keyword, final BoardCategory boardCategory,
                                                                                       final Pagination pagination, final int userId) {
        List<HomeBoardAllRes> boards =
                boardService.setUserInfoInAllRes(boardMapper.findBoardsByCategoryByKeywordOrderByRecommend(keyword, boardCategory, pagination), userId);
        boards = deleteOverlapBoard(boards);
        if (boards.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_SEARCH_RESULT);

        Collections.sort(boards);

        return DefaultRes.res(StatusCode.OK, ResponseMessage.SEARCH_BOARD, boards);
    }

    public List<HomeBoardAllRes> deleteOverlapBoard(final List<HomeBoardAllRes> boards){

        List<HomeBoardAllRes> list = boards;
        int prevId = -1;
        int saveId = 0;
        for( Iterator<HomeBoardAllRes> itr = list.iterator(); itr.hasNext(); )
        {
            HomeBoardAllRes board = itr.next();
            saveId = board.getBoardId();
            if(board.getBoardId() == prevId){
                itr.remove();
            }
            prevId = saveId;
        }
        return boards;
    }

}
