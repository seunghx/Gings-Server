package com.gings.controller;

import com.gings.dao.UserMapper;
import com.gings.domain.Directory;
import com.gings.domain.User;
import com.gings.model.DefaultRes;
import com.gings.model.Pagination;
import com.gings.model.SearchKeyword.SearchKeywordReq;
import com.gings.model.board.HomeBoard;
import com.gings.security.authentication.Authentication;
import com.gings.service.SearchService;
import com.gings.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@Authentication
public class SearchController {

    private final UserMapper userMapper;
    private final SearchService searchService;

    public SearchController(final UserMapper userMapper, final SearchService searchService) {
        this.userMapper = userMapper;
        this.searchService = searchService;
    }

    /**
     * 디렉토리 검색
     *
     * @return ResponseEntity
     */
    @GetMapping("search/directory")
    public ResponseEntity SearchDirectory(@RequestParam String keyword, final Pagination pagination) {
        try {
            DefaultRes<List<Directory>> defaultRes = searchService.selectUserByKeyword(keyword, pagination);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 보드 검색
     *
     * @return ResponseEntity
     */
    @GetMapping("search/boards")
    public ResponseEntity SearchBoards(@RequestParam String keyword, final Pagination pagination) {
        try {
            DefaultRes<List<HomeBoard.HomeBoardAllRes>> defaultRes = searchService.selectBoardByKeyword(keyword, pagination);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }
}
