package com.gings.service;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;
import com.gings.domain.Board;
import com.gings.domain.Directory;
import com.gings.domain.User;
import com.gings.model.DefaultRes;
import com.gings.model.Pagination;
import com.gings.model.SearchKeyword.SearchKeywordReq;
import com.gings.model.board.HomeBoard.HomeBoardAllRes;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SearchService {

    private final UserMapper userMapper;
    private final BoardMapper boardMapper;

    /**
     * 생성자 의존성 주입
     *
     * @param userMapper
     */
    public SearchService(final UserMapper userMapper, final BoardMapper boardMapper){
        this.userMapper = userMapper;
        this.boardMapper = boardMapper;
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
     * 디렉토리 검색
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<Directory>> selectUserByWriteTime(final Pagination pagination) {
        final List<Directory> users = userMapper.findUsersByWriteTime(pagination);
        if (users.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_INTRODUCE);

        for(int i = 0; i<users.size(); i++){
            if(users.get(i).getIntroduce().isEmpty()){
                users.remove(i);
            }
        }
        for(int i = 0; i<users.size(); i++){
            if(users.get(i).getIntroduce().isEmpty()){
                users.remove(i);
            }
        }

        return DefaultRes.res(StatusCode.OK, ResponseMessage.YES_INTRODUCE, users);
    }

    /**
     * 보드 검색(최신순)
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<HomeBoardAllRes>> selectBoardByKeywordByWriteTime(final String keyword, final Pagination pagination) {
        final List<HomeBoardAllRes> boards = boardMapper.findBoardsByKeywordOrderByWriteTime(keyword, pagination);
        for(HomeBoardAllRes board : boards) {
            board.setWriter(userMapper.findByUserId(board.getWriterId()).getName());
            board.setField(userMapper.findByUserId(board.getWriterId()).getField());
            board.setCompany(userMapper.findByUserId(board.getWriterId()).getCompany());
            board.setWriterImage(userMapper.selectProfileImg(board.getWriterId()).getImage());
        }
        if (boards.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_SEARCH_RESULT);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.SEARCH_BOARD, boards);
    }

    /**
     * 보드 검색(최신순)
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<HomeBoardAllRes>> selectBoardByKeywordByRecommend(final String keyword, final Pagination pagination) {
        final List<HomeBoardAllRes> boards = boardMapper.findBoardsByKeywordOrderByRecommend(keyword, pagination);
        for(HomeBoardAllRes board : boards) {
            board.setWriter(userMapper.findByUserId(board.getWriterId()).getName());
            board.setField(userMapper.findByUserId(board.getWriterId()).getField());
            board.setCompany(userMapper.findByUserId(board.getWriterId()).getCompany());
            board.setWriterImage(userMapper.selectProfileImg(board.getWriterId()).getImage());
        }
        if (boards.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_SEARCH_RESULT);

        Collections.sort(boards);

        return DefaultRes.res(StatusCode.OK, ResponseMessage.SEARCH_BOARD, boards);
    }

}
