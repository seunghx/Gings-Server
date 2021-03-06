package com.gings.service;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;

import com.gings.domain.User;
import com.gings.domain.board.Board;
import com.gings.domain.board.BoardReply;
import com.gings.model.DefaultRes;
import com.gings.model.MyPage.MyPageProfile;
import com.gings.model.MyPageBoard;
import com.gings.model.board.HomeBoard.HomeBoardOneRes;
import com.gings.model.board.HomeBoard.HomeBoardAllRes;
import com.gings.model.board.ModifyBoard.ModifyBoardReq;
import com.gings.model.Pagination;
import com.gings.model.board.ReBoard.ModifyReBoardReq;
import com.gings.model.board.ReBoard.ReBoardReq;
import com.gings.model.board.UpBoard.UpBoardReq;

import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import com.gings.utils.code.BoardCategory;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by YW
 */

@Slf4j
@Service
public class BoardService {

    private final BoardMapper boardMapper;
    private final UserMapper userMapper;
    private final S3MultipartServiceProd s3MultipartService;

    public BoardService(final BoardMapper boardMapper, final UserMapper userMapper,
                        final S3MultipartServiceProd s3MultipartService) {
        this.boardMapper = boardMapper;
        this.userMapper = userMapper;
        this.s3MultipartService = s3MultipartService;
    }


    /**
     * 전체 보드 조회
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<HomeBoardAllRes>> findAllBoard(final Pagination pagination, final int userId) {
        final List<HomeBoardAllRes> boards = setUserInfoInAllRes(boardMapper.findAllBoard(pagination), userId);
        if (boards.isEmpty()) {
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);
        }

        final List<HomeBoardAllRes> blockedBoards =  removeBlockedBoards(boards, userId);
        final List<HomeBoardAllRes> filteredBoards = removeBlackListBoards(blockedBoards, userId);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_ALL_BOARDS, filteredBoards);
    }

    /**
     * 보드 고유 번호로 보드 조회
     *
     * @param boardId 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<HomeBoardOneRes> findBoardByBoardId(final int boardId, final int userId) {
        HomeBoardOneRes board = setUserInfoInOneRes(boardMapper.findBoardByBoardId(boardId), userId);

        List<BoardReply> boardReplies = setUserInfoInReplyRes(boardMapper.findReplyByBoardId(boardId), userId);

        board.setReplys(boardReplies);

        if (board == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD, board);
    }


    /**
     * 카테고드별 보드 조회
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<HomeBoardAllRes>> findBoardsByCategoryByWriteTime(@Validated final BoardCategory category, final Pagination pagination, final int userId) {
        final List<HomeBoardAllRes> boards =
                setUserInfoInAllRes(boardMapper.findBoardsByCategory(category, pagination), userId);
        if (boards.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);

        final List<HomeBoardAllRes> blockedBoards =  removeBlockedBoards(boards, userId);
        final List<HomeBoardAllRes> filteredBoards = removeBlackListBoards(blockedBoards, userId);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_ALL_BOARDS, filteredBoards);
    }

    /**
     * 카테고드별 보드 조회(최신순)
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<HomeBoardAllRes>> findBoardsByCategoryByRecommend(@Validated final BoardCategory category, final Pagination pagination, final int userId) {
        final List<HomeBoardAllRes> boards =
                setUserInfoInAllRes(boardMapper.findBoardsByCategory(category, pagination), userId);

        if (boards.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);

        final List<HomeBoardAllRes> blockedBoards =  removeBlockedBoards(boards, userId);
        final List<HomeBoardAllRes> filteredBoards = removeBlackListBoards(blockedBoards, userId);
        Collections.sort(filteredBoards);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_ALL_BOARDS, filteredBoards);
    }

    /**
     * 보드 고유 번호로 보드 이미지 조회
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<String>> findImagesByBoardId(final int id) {
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
    public DefaultRes<List<String>> findKeywordsByBoardId(final int id) {
        final List<String> keywords = boardMapper.findKeywordsByBoardId(id);
        if (keywords.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD_INFO);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, keywords);
    }

    /**
     * 회원 고유 번호로 보드 찾기
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<List<MyPageBoard>> findBoardByUserId(final int id) {
        final List<MyPageBoard> boards = boardMapper.findBoardByUserId(id);
        if (boards == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD, boards);
    }

    public DefaultRes<List<MyPageBoard>> checkBoardByUser(final int id) {
        final List<MyPageBoard> boards = boardMapper.findBoardByUserId(id);
        if (boards == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.UNQUALIFIED, boards);
    }

    /**
     * 보드 고유 번호로 보드 추천수 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<Integer> countRecommendByBoardId(final int id) {
        final int recommend = boardMapper.countRecommendByBoardId(id);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, recommend);
    }

    /**
     * 보드 고유 번호로 보드 댓글 조회
     *
     * @param id 보드 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<List<BoardReply>> findReplyByBoardId(final int id) {
        final List<BoardReply> boardReplies = boardMapper.findReplyByBoardId(id);
        if (boardReplies.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD_INFO);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, boardReplies);
    }

    /**
     * 보드 댓글 고유 번호로 보드 댓글 좋아요수 조회
     *
     * @param id 보드 댓글 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findReplyRecommendNumbersByReplyId(final int id) {
        final int replyRecommend = boardMapper.findReplyRecommendNumbersByReplyId(id);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_BOARD_INFO, replyRecommend);
    }

    /**
     * 보드 저장
     *
     * @param upBoardReq 보드 데이터
     * @return DefaultRes
     */
    public DefaultRes saveBoard(@Validated final UpBoardReq upBoardReq) {
        try {
            boardMapper.saveBoard(upBoardReq);
            final int boardId = upBoardReq.getBoardId();

            if(upBoardReq.getImages() != null) {
                List<String> urlList = s3MultipartService.uploadMultipleFiles(upBoardReq.getImages());
                boardMapper.saveBoardImg(boardId, urlList);
            }
            if(upBoardReq.getKeywords() != null){
                boardMapper.saveBoardKeyword(boardId, upBoardReq.getKeywords());
            }

            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATE_BOARD);
        } catch (Exception e) {
            log.error("{}",e);
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 보드 좋아요 & 좋아요 취소
     *
     * @param boardId 보드
     * @param userId  회원 고유 번호
     * @return DefaultRes
     */

    public DefaultRes boardLikes(final int boardId, final int userId) {
        try {
            if (boardMapper.findBoardByBoardId(boardId) == null)
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);

            List<Integer> boardIdList = boardMapper.findRecommendBoardsByUserId(userId);

            Board.BoardRecommend boardLike = new Board.BoardRecommend();

            for (int id : boardIdList) {
                if (id == boardId) {
                    boardMapper.deleteBoardRecommender(boardId, userId);
                    boardLike.setRecommendBoardIdList(boardMapper.findBoardIdByRecommenderId(userId));
                    return DefaultRes.res(StatusCode.OK, ResponseMessage.CANCEL_LIKE_BOARD, boardLike);
                }
            }

            boardMapper.saveBoardRecommender(boardId, userId);
            boardLike.setRecommendBoardIdList(boardMapper.findBoardIdByRecommenderId(userId));
            return DefaultRes.res(StatusCode.OK, ResponseMessage.LIKE_BOARD, boardLike);

        } catch (Exception e) {
            log.error("{}",e);
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 보드 차단 & 차단 취소
     *
     * @param boardId 보드
     * @param userId  회원 고유 번호
     * @return DefaultRes
     */

    public DefaultRes boardBlocks(final int boardId, final int userId) {
        try {
            if (boardMapper.findBoardByBoardId(boardId) == null)
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);

            List<Integer> boardIdList = boardMapper.findBlockBoardsByUserId(userId);

            Board.BoardBlock boardBlock = new Board.BoardBlock();

            for (int id : boardIdList) {
                if (id == boardId) {
                    boardMapper.deleteBoardBlockUser(boardId, userId);
                    boardBlock.setBlockBoardIdList(boardMapper.findBlockBoardsByUserId(userId));
                    return DefaultRes.res(StatusCode.OK, ResponseMessage.CANCEL_BLOCK_BOARD, boardBlock);
                }
            }
            boardMapper.saveBoardBlockUser(boardId, userId);
            boardBlock.setBlockBoardIdList(boardMapper.findBlockBoardsByUserId(userId));
            return DefaultRes.res(StatusCode.OK, ResponseMessage.BLOCK_BOARD, boardBlock);
        } catch (Exception e) {
            log.error("{}",e);
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 유저 블랙리스트 추가
     *
     * @param boardId 보드
     * @param userId  회원 고유 번호
     * @return DefaultRes
     */


    public DefaultRes addBlackList(final int boardId, final int userId) {
        try {
            if (boardMapper.findBoardByBoardId(boardId) == null)
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);

            Board.Black black = new Board.Black();

            final int blockUserId = boardMapper.findBoardByBoardId(boardId).getWriterId();
            boardMapper.saveBlackListUser(userId, blockUserId);

            black.setBlackList(boardMapper.findBlackListUsersByUserId(userId));
            return DefaultRes.res(StatusCode.OK, ResponseMessage.ADD_BLACKLIST, black);
        } catch (Exception e) {
            log.error("{}",e);
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }


    /**
     * 보드 공유 개수 증가
     *
     * @param boardId 보드
     * @return DefaultRes
     */

    public DefaultRes increaseBoardShare(final int boardId) {
        try {
            if (boardMapper.findBoardByBoardId(boardId) == null)
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_BOARD);

            boardMapper.updateBoardShare(boardId);

            return DefaultRes.res(StatusCode.OK, ResponseMessage.SHARE_BOARD);

        } catch (Exception e){
            log.error("{}",e);
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 리보드 저장
     *
     * @param reBoardReq 보드 데이터
     * @return DefaultRes
     */

    public DefaultRes saveReBoard(@Validated final ReBoardReq reBoardReq) {
        try {
            boardMapper.saveReBoard(reBoardReq);
            final int reReplyId = reBoardReq.getReplyId();

            if(reBoardReq.getImages() != null) {
                List<String> urlList = s3MultipartService.uploadMultipleFiles(reBoardReq.getImages());
                boardMapper.saveReBoardImg(reReplyId, urlList);
            }
            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATE_REBOARD);
        } catch (Exception e) {
            log.error("{}",e);
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 리보드 좋아요 & 좋아요 취소
     *
     * @param replyId 보드
     * @param userId  회원 고유 번호
     * @return DefaultRes
     */

    public DefaultRes ReBoardLikes(final int replyId, final int userId) {
        try {
            List<Integer> reBoardIdList = boardMapper.findRecommendReBoardsByUserId(userId);
            for (int id : reBoardIdList) {
                if (id == replyId) {
                    boardMapper.deleteReBoardRecommender(replyId, userId);
                    return DefaultRes.res(StatusCode.OK, ResponseMessage.CANCEL_LIKE_REBOARD);
                }
            }
            boardMapper.saveReBoardRecommender(replyId, userId);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.LIKE_REBOARD);
        } catch (Exception e) {
            log.error("{}",e);
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 보드 수정
     *
     * @param boardId        보드 데이터
     * @param modifyBoardReq 보드 데이터
     * @return DefaultRes
     */

    public DefaultRes updateBoard(final int boardId, final @Validated ModifyBoardReq modifyBoardReq) {
        try {
            boardMapper.updateBoard(boardId, modifyBoardReq);
            if(modifyBoardReq.getPrevImagesUrl() != null) {
                for (String url : modifyBoardReq.getPrevImagesUrl()) {
                    boardMapper.deleteBoardImg(url);
                }
                s3MultipartService.deleteMultipleFiles(modifyBoardReq.getPrevImagesUrl());
            }
            if(modifyBoardReq.getPostImages() != null) {
                List<String> urlList = s3MultipartService.uploadMultipleFiles(modifyBoardReq.getPostImages());
                boardMapper.saveBoardImg(boardId, urlList);
            }

            if(modifyBoardReq.getPrevKeywords() != null) {
                for (String keywords : modifyBoardReq.getPrevKeywords()) {
                    boardMapper.deleteBoardKeyword(keywords);
                }
            }
            if(modifyBoardReq.getPostKeywords() != null) {
                boardMapper.saveBoardKeyword(boardId, modifyBoardReq.getPostKeywords());
            }

            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.UPDATE_BOARD);

        } catch (Exception e) {
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 리보드 수정
     *
     * @param replyId          보드 데이터
     * @param modifyReBoardReq 보드 데이터
     * @return DefaultRes
     */

    public DefaultRes updateReBoard(final int replyId, @Validated final ModifyReBoardReq modifyReBoardReq) {
        try {
            boardMapper.updateReBoard(replyId, modifyReBoardReq);

            List<String> imgUrl = modifyReBoardReq.getPrevImagesUrl();

            if(imgUrl != null){
                for (String url : modifyReBoardReq.getPrevImagesUrl()) {
                    boardMapper.deleteReBoardImg(url);
                }
                s3MultipartService.deleteMultipleFiles(modifyReBoardReq.getPrevImagesUrl());
            }

            List<MultipartFile> img = modifyReBoardReq.getPostImages();

            if(img != null) {
                List<String> urlList = s3MultipartService.uploadMultipleFiles(modifyReBoardReq.getPostImages());
                boardMapper.saveReBoardImg(replyId, urlList);
            }

            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.UPDATE_REBOARD);
        } catch (Exception e) {
            log.error("{}", e);
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public List<HomeBoardAllRes> setUserInfoInAllRes(@Validated List<HomeBoardAllRes> boards, int userId) {
        List<Integer> likedBoardIdList = boardMapper.findRecommendBoardsByUserId(userId);

        for (HomeBoardAllRes board : boards) {
            for (int likedBoardId : likedBoardIdList) {
                if (board.isLikeChk()) break;

                if (board.getBoardId() == likedBoardId){ board.setLikeChk(true); }
                else { board.setLikeChk(false); }
            }
            
            MyPageProfile profile = userMapper.selectProfileImg(board.getWriterId());
            String image = profile == null? null : profile.getImage();
            
            board.setWriter(userMapper.findByUserId(board.getWriterId()).getName());
            board.setField(userMapper.findByUserId(board.getWriterId()).getField());
            board.setCompany(userMapper.findByUserId(board.getWriterId()).getCompany());
            board.setWriterImage(image);
        }
        return boards;
    }

    public HomeBoardOneRes setUserInfoInOneRes(@Validated HomeBoardOneRes board, int userId) {
        List<Integer> likedBoardIdList = boardMapper.findRecommendBoardsByUserId(userId);

        for(int likedBoardId : likedBoardIdList) {
            if (board.isLikeChk()) break;

            if (board.getBoardId() == likedBoardId) {
                board.setLikeChk(true);
            } else {
                board.setLikeChk(false);
            }

            User user = userMapper.findByUserId(board.getWriterId());

            if(user.getName() != null &&!(user.equals(""))) board.setWriter(user.getName());
            if(user.getField() != null &&!(user.equals(""))) board.setField(user.getField());
            if(user.getCompany() != null &&!(user.equals(""))) board.setCompany(user.getCompany());


            if(userMapper.selectProfileImg(board.getWriterId()) != null) {
                MyPageProfile profile = userMapper.selectProfileImg(board.getWriterId());
                if (profile.getImage() != null &&
                        !(profile.getImage().isEmpty())) {
                    board.setWriterImage(userMapper.selectProfileImg(board.getWriterId()).getImage());
                }
            }
        }
        List<Integer> likedReBoardIdList = boardMapper.findRecommendRepliesByUserId(userId);
        for(BoardReply reboard : board.getReplys()){
            for(int likedReBoardId : likedReBoardIdList){
                if(reboard.isLikeChk()) break;
                if(reboard.getReplyId() == likedReBoardId){
                    reboard.setLikeChk(true);
                }
                else{
                    reboard.setLikeChk(false);
                }
            }
        }
        return board;
    }



    public List<BoardReply> setUserInfoInReplyRes(@Validated List<BoardReply> boardReplies, int userId) {


        List<Integer> likedReBoardIdList = boardMapper.findRecommendReBoardsByUserId(userId);

        for (BoardReply boardReply : boardReplies) {
            for (int likedReBoardId : likedReBoardIdList) {
                if (boardReply.isLikeChk()) break;

                if (boardReply.getReplyId() == likedReBoardId){ boardReply.setLikeChk(true); }
                else { boardReply.setLikeChk(false); }
            }

            User user = userMapper.findByUserId(boardReply.getWriterId());

            if(user.getName() != null && !(user.getName().equals(""))) boardReply.setWriter(user.getName());
            if(userMapper.selectProfileImg(boardReply.getWriterId()) != null) {
                MyPageProfile profile = userMapper.selectProfileImg(boardReply.getWriterId());
                if (profile.getImage() != null &&
                        !(profile.getImage().isEmpty())) {
                    boardReply.setWriterImage(userMapper.selectProfileImg(boardReply.getWriterId()).getImage());
                }
            }
        }
        return boardReplies;
    }

    public List<HomeBoardAllRes> removeBlockedBoards(@Validated List<HomeBoardAllRes> boards, int userId) {

        List<Integer> blockBoardIdList = boardMapper.findBlockBoardsByUserId(userId);

        List<HomeBoardAllRes> filteredBoards = 
                boards.stream()
                      .filter(board -> !blockBoardIdList.contains(board.getBoardId()))
                      .collect(Collectors.toList());

        return filteredBoards;
    }

    public List<HomeBoardAllRes> removeBlackListBoards(@Validated List<HomeBoardAllRes> boards, int userId) {

        List<Integer> blackListUserList = boardMapper.findBlackListUsersByUserId(userId);
        List<Integer> blockBoardIdList = new ArrayList<>();
        
        if(blackListUserList == null) {
            log.info("Black list is null");
        }
        
        for(int blackListUserId : blackListUserList){
            for(int boardId : boardMapper.findBoardIdByUserId(blackListUserId)){
                blockBoardIdList.add(boardId);
            }
        }

        return boards.stream()
                     .filter(board -> !blockBoardIdList.contains(board.getBoardId())).collect(Collectors.toList());

    }
}

