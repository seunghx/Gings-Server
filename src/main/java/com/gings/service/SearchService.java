package com.gings.service;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;
import com.gings.domain.Directory;
import com.gings.domain.User;
import com.gings.model.DefaultRes;
import com.gings.model.Pagination;
import com.gings.model.SearchKeyword.SearchKeywordReq;
import com.gings.model.board.HomeBoard;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SearchService {

    private final UserMapper userMapper;

    /**
     * 생성자 의존성 주입
     *
     * @param userMapper
     */
    public SearchService(final UserMapper userMapper){
        this.userMapper = userMapper;
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

}
