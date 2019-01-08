package com.gings.controller;

import com.gings.dao.UserMapper;
import com.gings.domain.Directory;
import com.gings.domain.User;
import com.gings.model.DefaultRes;
import com.gings.model.Pagination;
import com.gings.model.SearchKeyword.SearchKeywordReq;
import com.gings.model.board.HomeBoard;
import com.gings.security.GingsPrincipal;
import com.gings.security.authentication.Authentication;
import com.gings.service.SearchService;
import com.gings.service.UserService;
import com.gings.utils.code.BoardCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * 최신순 디렉토리 목록
     *
     * @return ResponseEntity
     */
    @GetMapping("search/directory/new")
    public ResponseEntity getAllDirectoryByWriteTime(final Pagination pagination) {
        try {
            DefaultRes<List<Directory>> defaultRes = searchService.selectUserByWriteTime(pagination);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 보드 검색(최신순)
     *
     * @return ResponseEntity
     */
    @GetMapping("search/boards/latest")
    public ResponseEntity SearchBoardsByLatest(@RequestParam String keyword, final Pagination pagination, final GingsPrincipal principal) {
        try {
            final int userId = principal.getUserId();
            log.error("userid : " + userId);
            DefaultRes<List<HomeBoard.HomeBoardAllRes>> defaultRes =
                    searchService.selectBoardByKeywordByWriteTime(keyword, pagination, userId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 보드 검색(추천순)
     *
     * @return ResponseEntity
     */
    @GetMapping("search/boards/recommend")
    public ResponseEntity SearchBoardsByRecommend(@RequestParam String keyword, final Pagination pagination, GingsPrincipal principal) {
        try {
            final int userId = principal.getUserId();
            DefaultRes<List<HomeBoard.HomeBoardAllRes>> defaultRes =
                    searchService.selectBoardByKeywordByRecommend(keyword, pagination, userId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 카테고리별 보드 검색(최신순)
     *
     * @return ResponseEntity
     */
    @GetMapping("search/boards/category/{category}/latest")
    public ResponseEntity SearchBoardsByCategoryByWriteTime(@RequestParam String keyword, @PathVariable BoardCategory category,
                                                            final Pagination pagination, GingsPrincipal principal) {
        try {

            log.error("keyword : " + keyword + "category : " + category.toString() + "userid : " + principal.getUserId());

            final int userId = principal.getUserId();

            DefaultRes<List<HomeBoard.HomeBoardAllRes>> defaultRes =
                    searchService.selectBoardByCategoryByKeywordByWriteTime(keyword, category, pagination, userId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 카테고리별 보드 검색(추천순)
     *
     * @return ResponseEntity
     */
    @GetMapping("search/boards/category/{category}/recommend")
    public ResponseEntity SearchBoardsByCategoryByRecommend(@RequestParam String keyword, @PathVariable BoardCategory category,
                                                            final Pagination pagination, GingsPrincipal principal) {
        try {
            final int userId = principal.getUserId();
            DefaultRes<List<HomeBoard.HomeBoardAllRes>> defaultRes =
                    searchService.selectBoardByCategoryByKeywordByRecommend(keyword, category, pagination, userId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }
}
