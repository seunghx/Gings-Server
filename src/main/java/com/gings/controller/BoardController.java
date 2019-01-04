package com.gings.controller;

import com.gings.domain.Board;

import com.gings.model.DefaultRes;
import com.gings.model.board.HomeBoard.HomeBoardAllRes;
import com.gings.model.board.HomeBoard.HomeBoardOneRes;

import com.gings.model.board.ModifyBoard.ModifyBoardReq;
import com.gings.model.Pagination;
import com.gings.model.board.UpBoard.UpBoardOneRes;
import com.gings.model.board.UpBoard.UpBoardReq;
import com.gings.model.board.ReBoard.ReBoardReq;

import com.gings.security.Principal;
import com.gings.security.authentication.Authentication;

import com.gings.service.BoardService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@Authentication

public class BoardController {

    private final BoardService boardService;

    public BoardController(final BoardService boardService) {
        this.boardService = boardService;
    }


    /**
     * 모든 보드 조회
     *
     * @param pagination 페이지네이션
     * @return ResponseEntity
     */
    @GetMapping("boards")
    public ResponseEntity getAllBoards(final Pagination pagination) {
        try {
            DefaultRes<List<HomeBoardAllRes>> defaultRes = boardService.findAllBoard(pagination);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.OK);
        }
    }

    /**
     * 보드 고유 번호로 보드 조회
     *
     * @param boardId 보드 고유 번호
     * @return ResponseEntity
     */
    @GetMapping("boards/{boardId}")
    public ResponseEntity getBoardByBoardId(@PathVariable("boardId") final int boardId) {
        try {
            DefaultRes<HomeBoardOneRes> defaultRes = boardService.findBoardByBoardId(boardId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.OK);
        }
    }

    /**
     * 보드 저장
     *
     * @param upBoardReq 보드 데이터
     * @param  principal jwt
     * @return ResponseEntity
     */
    @PostMapping("boards")
    public ResponseEntity saveBoard(final UpBoardReq upBoardReq, final Principal principal) {
        try {
            upBoardReq.setWriterId(principal.getUserId());
            return new ResponseEntity<>(boardService.saveBoard(upBoardReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.OK);
        }
    }

    /**
     * 보드 추천
     *
     * @param boardId 보드 고유 번호
     * @return ResponseEntity
     */
    @PostMapping("boards/{boardId}/recommend")
    public ResponseEntity likeBoard(@PathVariable("boardId") final int boardId, final Principal principal) {
        try {
            return new ResponseEntity<>(boardService.BoardLikes(boardId, principal.getUserId()), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.OK);
        }
    }

    /**
     * 리보드 저장
     *
     * @param reBoardReq 보드 데이터
     * @return ResponseEntity
     */

    @PostMapping("reboards")
    public ResponseEntity saveReBoard(final ReBoardReq reBoardReq, final Principal principal) {
        try {
            reBoardReq.setWriterId(principal.getUserId());
            return new ResponseEntity<>(boardService.saveReBoard(reBoardReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.OK);
        }
    }

    /**
     * 리보드 추천
     *
     * @param reboardId 보드 고유 번호
     * @return ResponseEntity
     */
    @PostMapping("reboards/{reboardId}/recommend")
    public ResponseEntity likeReBoard(@PathVariable("reboardId") final int reboardId,
                                      final Principal principal) {
        try {
            return new ResponseEntity<>(boardService.ReBoardLikes(reboardId, principal.getUserId()), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.OK);
        }
    }

    /**
     * 보드 수정
     *
     * @param modifyBoardReq 수정할 보드
     * @return ResponseEntity
     */
    @PutMapping("boards/{boardId}")
    public ResponseEntity updateBoard(@PathVariable final int boardId, final ModifyBoardReq modifyBoardReq,
                                      Principal principal) {
        try {
            modifyBoardReq.setWriterId(principal.getUserId());
            return new ResponseEntity<>(boardService.updateBoard(boardId,modifyBoardReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.OK);
        }
    }

}
