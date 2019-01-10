package com.gings.controller;

import com.gings.dao.BoardMapper;

import com.gings.model.DefaultRes;
import com.gings.model.board.HomeBoard.HomeBoardAllRes;
import com.gings.model.board.HomeBoard.HomeBoardOneRes;
import com.gings.model.board.ModifyBoard.ModifyBoardReq;
import com.gings.model.Pagination;
import com.gings.model.board.ReBoard.ModifyReBoardReq;
import com.gings.model.board.UpBoard.UpBoardReq;
import com.gings.model.board.ReBoard.ReBoardReq;

import com.gings.security.GingsPrincipal;
import com.gings.security.authentication.Authentication;

import com.gings.service.BoardService;

import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import com.gings.utils.code.BoardCategory;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;

/**
 * Created by YW
 */

@Slf4j
@RestController
@Authentication
public class BoardController {

    private final BoardService boardService;
    private final BoardMapper boardMapper;

    public BoardController(final BoardService boardService, final BoardMapper boardMapper) {
        this.boardService = boardService;
        this.boardMapper = boardMapper;
    }

    /**
     * 모든 보드 조회
     *
     * @param pagination 페이지네이션
     * @param principal jwt
     * @return ResponseEntity
     */
    @GetMapping("boards")
    public ResponseEntity getAllBoards(final Pagination pagination, final GingsPrincipal principal) {
        try {
            final int userId = principal.getUserId();
            DefaultRes<List<HomeBoardAllRes>> defaultRes = boardService.findAllBoard(pagination, userId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 보드 고유 번호로 보드 조회
     *
     * @param boardId 보드 고유 번호
     * @param principal jwt
     * @return ResponseEntity
     */
    @GetMapping("boards/{boardId}")
    public ResponseEntity getBoardByBoardId(@PathVariable("boardId") final int boardId, final GingsPrincipal principal) {
        try {
            final int userId = principal.getUserId();
            DefaultRes<HomeBoardOneRes> defaultRes = boardService.findBoardByBoardId(boardId, userId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 카테고리별 모든 보드 조회(최신순)
     *
     * @param category 보드 카테고리
     * @param pagination 페이지네이션
     * @param principal jwt
     * @return ResponseEntity
     */
    @GetMapping("boards/category/{category}/latest")
    public ResponseEntity getBoardsByCategory(@PathVariable BoardCategory category, final Pagination pagination,
                                              final GingsPrincipal principal) {
        try {
            final int userId = principal.getUserId();
            DefaultRes<List<HomeBoardAllRes>> defaultRes = boardService.findBoardsByCategoryByWriteTime(category, pagination, userId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 카테고리별 모든 보드 조회(추천순)
     *
     * @param category 보드 카테고리
     * @param pagination 페이지네이션
     * @param principal jwt
     * @return ResponseEntity
     */
    @GetMapping("boards/category/{category}/recommend")
    public ResponseEntity getBoardsByCategoryByRecommend(@PathVariable BoardCategory category, final Pagination pagination,
                                              final GingsPrincipal principal) {
        try {
            final int userId = principal.getUserId();
            DefaultRes<List<HomeBoardAllRes>> defaultRes = boardService.findBoardsByCategoryByRecommend(category, pagination, userId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 보드 저장
     *
     * @param upBoardReq 업보드 데이터
     * @param principal jwt
     * @return ResponseEntity
     */
    @PostMapping("boards")
    public ResponseEntity saveBoard(final UpBoardReq upBoardReq, final GingsPrincipal principal) {
        try {
            upBoardReq.setWriterId(principal.getUserId());
            return new ResponseEntity<>(boardService.saveBoard(upBoardReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 보드 추천
     *
     * @param boardId 보드 고유 번호
     * @param principal jwt
     * @return ResponseEntity
     */
    @PostMapping("boards/{boardId}/recommend")
    public ResponseEntity likeBoard(@PathVariable("boardId") final int boardId, final GingsPrincipal principal) {
        try {
            return new ResponseEntity<>(boardService.boardLikes(boardId, principal.getUserId()), HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 보드 가리기
     *
     * @param boardId 보드 고유 번호
     * @param principal jwt
     * @return ResponseEntity
     */
    @PostMapping("boards/{boardId}/block")
    public ResponseEntity blockBoard(@PathVariable("boardId") final int boardId, final GingsPrincipal principal) {
        try {
            return new ResponseEntity<>(boardService.boardBlocks(boardId, principal.getUserId()), HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 블랙리스트 추가
     *
     * @param boardId 보드 고유 번호
     * @param principal jwt
     * @return ResponseEntity
     */
    @PostMapping("boards/{boardId}/blacklist")
    public ResponseEntity addBlackList(@PathVariable("boardId") final int boardId, final GingsPrincipal principal) {
        try {
            return new ResponseEntity<>(boardService.addBlackList(boardId, principal.getUserId()), HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 보드 공유
     *
     * @param boardId 보드 고유 번호
     * @return ResponseEntity
     */
    @PostMapping("boards/{boardId}/share")
    public ResponseEntity shareBoard(@PathVariable("boardId") final int boardId) {
        try {
            return new ResponseEntity<>(boardService.increaseBoardShare(boardId), HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 리보드 저장
     *
     * @param reBoardReq 리보드 데이터
     * @param principal jwt
     * @return ResponseEntity
     */

    @PostMapping("reboards")
    public ResponseEntity saveReBoard(final ReBoardReq reBoardReq, final GingsPrincipal principal) {
        try {
            reBoardReq.setWriterId(principal.getUserId());
            return new ResponseEntity<>(boardService.saveReBoard(reBoardReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 리보드 추천
     *
     * @param reboardId 보드 고유 번호
     * @param principal jwt
     * @return ResponseEntity
     */
    @PostMapping("reboards/{reboardId}/recommend")
    public ResponseEntity likeReBoard(@PathVariable("reboardId") final int reboardId,
                                      final GingsPrincipal principal) {
        try {
            return new ResponseEntity<>(boardService.ReBoardLikes(reboardId, principal.getUserId()), HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 보드 수정
     *
     * @param modifyBoardReq 수정할 보드 데이터
     * @param principal jwt
     * @return ResponseEntity
     */
    @PutMapping("boards/{boardId}")
    public ResponseEntity updateBoard(@PathVariable final int boardId, final ModifyBoardReq modifyBoardReq,
                                      GingsPrincipal principal) {
        try {
            modifyBoardReq.setWriterId(principal.getUserId());
            return new ResponseEntity<>(boardService.updateBoard(boardId,modifyBoardReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 리보드 수정
     *
     * @param reboardId 리보드 고유 번호
     * @param modifyReBoardReq 수정할 보드
     * @param principal jwt
     * @return ResponseEntity
     */
    @PutMapping("reboards/{reboardId}")
    public ResponseEntity updateReBoard(@PathVariable final int reboardId, final ModifyReBoardReq modifyReBoardReq,
                                      GingsPrincipal principal) {
        try {
            modifyReBoardReq.setWriterId(principal.getUserId());
            return new ResponseEntity<>(boardService.updateReBoard(reboardId,modifyReBoardReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 보드 삭제
     *
     * @param boardId 보드 고유 번호
     * @return ResponseEntity
     */
    @DeleteMapping("boards/{boardId}")
    public ResponseEntity deleteBoard(@PathVariable final int boardId) {
        try {
            boardMapper.deleteBoard(boardId);
            DefaultRes defaultRes = DefaultRes.res(StatusCode.OK, ResponseMessage.DELETE_BOARD);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 리보드 삭제
     *
     * @param reboardId 리보드 고유 번호
     * @return ResponseEntity
     */
    @DeleteMapping("reboards/{reboardId}")
    public ResponseEntity deleteReBoard(@PathVariable final int reboardId) {
        try {
            boardMapper.deleteReBoard(reboardId);
            DefaultRes defaultRes = DefaultRes.res(StatusCode.OK, ResponseMessage.DELETE_REBOARD);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }
}
