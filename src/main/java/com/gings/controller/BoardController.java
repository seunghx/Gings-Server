package com.gings.controller;

import com.gings.domain.Board;
import com.gings.domain.BoardKeyword;
import com.gings.domain.BoardReply;
import com.gings.model.DefaultRes;
import com.gings.model.ModifyBoard.ModifyBoardReq;
import com.gings.model.Pagination;
import com.gings.model.UpBoard.UpBoardReq;
import com.gings.model.ReBoard.ReBoardReq;
import com.gings.service.BoardService;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.AbstractDocument;
import javax.validation.Valid;
import java.util.List;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
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
    public ResponseEntity getAllBoards(@RequestBody  final Pagination pagination) {
        try {
            DefaultRes<List<Board>> defaultRes = boardService.findAllBoard(pagination);
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
            DefaultRes<Board> defaultRes = boardService.findBoardByBoardId(boardId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 보드 고유 번호로 보드 이미지 조회
     *
     * @param boardId 보드 고유 번호
     * @return ResponseEntity
     */
    @GetMapping("boards/{boardId}/images")
    public ResponseEntity getImagesByBoardId(@PathVariable("boardId") final int boardId) {
        try {
            DefaultRes<List<String>> defaultRes = boardService.findImagesByBoardId(boardId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 보드 고유 번호로 보드 키워드 조회
     *
     * @param boardId 보드 고유 번호
     * @return ResponseEntity
     */
    @GetMapping("boards/{boardId}/keywords")
    public ResponseEntity getKeywordsByBoardId(@PathVariable("boardId") final int boardId) {
        try {
            DefaultRes<List<String>> defaultRes = boardService.findKeywordsByBoardId(boardId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 보드 고유 번호로 보드 추천수 조회
     *
     * @param boardId 보드 고유 번호
     * @return ResponseEntity
     */
    @GetMapping("boards/{boardId}/recommend")
    public ResponseEntity getRecommendCountByBoardId(@PathVariable("boardId") final int boardId) {
        try {
            DefaultRes<Integer> defaultRes = boardService.countRecommendByBoardId(boardId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 보드 고유 번호로 보드 댓글 조회
     *
     * @param boardId 보드 고유 번호
     * @return ResponseEntity
     */
    @GetMapping("boards/{boardId}/replys")
    public ResponseEntity getReplyByBoardId(@PathVariable("boardId") final int boardId) {
        try {
            DefaultRes<List<BoardReply>> defaultRes = boardService.findReplyByBoardId(boardId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 댓글 고유 번호로 댓글 좋아요수 조회
     *
     * @param replyId 보드 고유 번호
     * @return ResponseEntity
     */
    @GetMapping("replies/{replyId}/replyRecommend")
    public ResponseEntity findReplyRecommendNumbers(@PathVariable("boardId") final int replyId) {
        try {
            DefaultRes<Integer> defaultRes = boardService.findReplyRecommendNumbersByReplyId(replyId);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 보드 저장
     *
     * @param upBoardReq 보드 데이터
     * @return ResponseEntity
     */
    @PostMapping("boards")
    public ResponseEntity saveBoard(final UpBoardReq upBoardReq) {
        try {
            return new ResponseEntity<>(boardService.saveBoard(upBoardReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 보드 추천
     *
     * @param boardId 보드 고유 번호
     * @return ResponseEntity
     */
    @PostMapping("boards/{boardId}/recommend")
    public ResponseEntity likeBoard(@PathVariable("boardId") final int boardId) {
        try {
            final int userId = 1; // token 값으로 대체
            return new ResponseEntity<>(boardService.BoardLikes(boardId, userId), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 리보드 저장
     *
     * @param reBoardReq 보드 데이터
     * @return ResponseEntity
     */

    @PostMapping("replies")
    public ResponseEntity saveReBoard(final ReBoardReq reBoardReq) {
        try {
            return new ResponseEntity<>(boardService.saveReBoard(reBoardReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 리보드 추천
     *
     * @param replyId 보드 고유 번호
     * @return ResponseEntity
     */
    @PostMapping("replies/{replyId}/recommend")
    public ResponseEntity likeReBoard(@PathVariable("replyId") @RequestBody final int replyId) {
        try {
            final int userId = 1; // token 값으로 대체
            return new ResponseEntity<>(boardService.ReBoardLikes(replyId, userId), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("boards/{boardId}")
    public ResponseEntity updateBoard(@PathVariable final int boardId, final ModifyBoardReq modifyBoardReq) {
        try {
            return new ResponseEntity<>(boardService.updateBoard(boardId,modifyBoardReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
