package com.gings.controller;

import com.gings.domain.Board;
import com.gings.model.DefaultRes;
import com.gings.model.GuestModel;
import com.gings.model.IntroduceModel;
import com.gings.model.MyPage;
import com.gings.security.Principal;
import com.gings.security.authentication.Authentication;
import com.gings.service.BoardService;
import com.gings.service.MyPageService;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@RequestMapping("mypage")
@Authentication
public class MyPageController {
    private final MyPageService myPageService;
    private final BoardService boardService;

    public MyPageController(MyPageService myPageService, BoardService boardService) {
        this.myPageService = myPageService;
        this.boardService = boardService;
    }

    /**
     * 마이페이지 회원 고유 번호로 상단 정보 출력
     *
     * @param principal 토큰으로 회원 아이디 가져오기
     * @return ResponseEntity
     */
    @GetMapping("/{myPageUserId}")
    public ResponseEntity getUser(@PathVariable("myPageUserId") final int myPageUserId, final Principal principal){
        try{
            final int id = principal.getUserId();
            if(id != myPageUserId){
                DefaultRes<MyPage>defaultRes = myPageService.checkUserId(myPageUserId);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }else {
                DefaultRes<MyPage> defaultRes = myPageService.findByUserId(id);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 마이페이지 회원 고유 번호로 중간 정보 출력
     *
     * @param principal 토큰으로 회원 아이디 가져오기
     * @return ResponseEntity
     */
    @GetMapping("/introduce/{myPageUserId}")
    public ResponseEntity getUserIntro(@PathVariable("myPageUserId") final int myPageUserId, final Principal principal){
        try{
            final int id = principal.getUserId();
            if(id != myPageUserId){
                DefaultRes<MyPage.MyPageIntro>defaultRes = myPageService.checkuserIntroduce(myPageUserId);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }else {
                DefaultRes<MyPage.MyPageIntro> defaultRes = myPageService.userIntroduce(id);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active/{myPageUserId}")
    public ResponseEntity getUserActive(@PathVariable("myPageUserId") final int myPageUserId, final Principal principal){
        try{
            final int id = principal.getUserId();
            if(id != myPageUserId){
                DefaultRes<List<Board>>defaultRes = boardService.checkBoardByUser(myPageUserId);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }else {
                DefaultRes<List<Board>>defaultRes = boardService.findBoardByUserId(id);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//==================================게스트 보드 조회===========================================================
    @GetMapping("/guestboard/{myPageUserId}")
    public ResponseEntity findGuestBoards(@PathVariable("myPageUserId") final int myPageUserId, final Principal principal){
        try{
            final int id = principal.getUserId();
            if(id == myPageUserId){
                DefaultRes<List<GuestModel.GuestModelRes>>defaultRes = myPageService.checkfindGuestBoard(myPageUserId);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }else {
                DefaultRes<List<GuestModel.GuestModelRes>>defaultRes = myPageService.findGuestBoard(id);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/guestboard/{myPageUserId}")
    public ResponseEntity saveGuestBoard(@PathVariable("myPageUserId") final int myPageUserId, final GuestModel.GuestModelReq guestModelReq, final Principal principal){
        try{
            final int id = principal.getUserId();
            if(id == myPageUserId){
                DefaultRes defaultRes = myPageService.checkUser();
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }else
                return new ResponseEntity<>(myPageService.createGuest(guestModelReq, id), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 //============================================ 자기소개 수정========================================================

    //여기서도 토큰 id랑 수정하려는 사랑 id chk
    @GetMapping("/changeIntro/{myPageUserId}")
    public ResponseEntity tryToChangeIntroduce(@PathVariable("myPageUserId") final int myPageUserId, final Principal principal){
        try{
            final int id = principal.getUserId();
            if(id != myPageUserId){
                DefaultRes defaultRes = myPageService.checkUser();
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }else {
                DefaultRes<IntroduceModel.IntroduceRes> defaultRes = myPageService.selectIntroduce(id);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/changeIntro/{myPageUserId}")
    public ResponseEntity changeIntroduce(@PathVariable("myPageUserId") final int myPageUserId, final IntroduceModel.IntroduceReq introduceReq,final Principal principal){
        try {
            final int id = principal.getUserId();
            if(id != myPageUserId){
                DefaultRes defaultRes = myPageService.checkUser();
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }else
                return new ResponseEntity<>(myPageService.changeUserIntroduce(id, introduceReq), HttpStatus.OK);
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //===============================프로필 사진 변경====================================================
    //===============================프로필 정보 수정====================================================
    //



}
