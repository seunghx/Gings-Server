package com.gings.controller;

import com.gings.dao.UserMapper;
import com.gings.model.DefaultRes;
import com.gings.model.GuestModel;
import com.gings.model.IntroduceModel;
import com.gings.model.MyPage;
import com.gings.security.*;
import com.gings.model.*;
import com.gings.security.authentication.Authentication;
import com.gings.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@RequestMapping("mypage")
@Authentication
public class MyPageController {
    private final UserMapper userMapper;
    private final MyPageService myPageService;
    private final BoardService boardService;
    private final PasswordEncoder passwordEncoder;
    private final FCMService fcmService;
    private final AlarmService alarmService;

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;


    public MyPageController(UserMapper userMapper, MyPageService myPageService, BoardService boardService,
                            PasswordEncoder passwordEncoder, FCMService fcmService, AlarmService alarmService) {
        this.userMapper = userMapper;
        this.myPageService = myPageService;
        this.boardService = boardService;
        this.passwordEncoder = passwordEncoder;
        this.fcmService = fcmService;
        this.alarmService = alarmService;
    }


            //====================================== 마이 페이지 ====================================================
            /**
             * 자신의 마이페이지 회원 고유 번호로 상단 정보 출력
             *
             * @param principal 토큰으로 회원 아이디 가져오기
             * @return ResponseEntity
             */
            @GetMapping("/mine")
            public ResponseEntity getMyUser(final GingsPrincipal principal){
                try{
                    final int id = principal.getUserId();
                    DefaultRes<MyPage> defaultRes = myPageService.findByUserId(id);
                    return new ResponseEntity<>(defaultRes, HttpStatus.OK);
                }catch (Exception e){
                    log.error(e.getMessage());
                    return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            /**
             * 타인의 마이페이지 회원 고유 번호로 상단 정보 출력
             *
             * @param myPageUserId
             * @param principal 토큰으로 회원 아이디 가져오기
             * @return ResponseEntity
             */
            @GetMapping("others/{myPageUserId}")
            public ResponseEntity getOtherUser(@PathVariable("myPageUserId") final int myPageUserId, final GingsPrincipal principal){
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
     * 자신의 마이페이지 회원 고유 번호로 중간 정보 출력
     *
     * @param principal 토큰으로 회원 아이디 가져오기
     * @return ResponseEntity
     */
    @GetMapping("mine/introduce")
    public ResponseEntity getMyUserIntro(final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            DefaultRes<MyPage.MyPageIntro> defaultRes = myPageService.userIntroduce(id);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 타인의 마이페이지 회원 고유 번호로 중간 정보 출력
     *
     * @param myPageUserId
     * @param principal 토큰으로 회원 아이디 가져오기
     * @return ResponseEntity
     */
    @GetMapping("others/introduce/{myPageUserId}")
    public ResponseEntity getOtherUserIntro(@PathVariable("myPageUserId") final int myPageUserId, final GingsPrincipal principal){
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

    /**
     * 자신의 마이페이지 활동 보드 조회
     *
     * @param principal
     * @return ResponseEntity
     */
    @GetMapping("mine/active")
    public ResponseEntity getUserActive(final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            DefaultRes<List<MyPageBoard>>defaultRes = boardService.findBoardByUserId(id);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 타인의 마이페이지 회원 고유 번호로 활동 보드 출력
     * @param myPageUserId
     * @param principal
     * @return ResponseEntity
     */
    @GetMapping("others/active/{myPageUserId}")
    public ResponseEntity getUserActive(@PathVariable("myPageUserId") final int myPageUserId, final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            if(id != myPageUserId){
                DefaultRes<List<MyPageBoard>>defaultRes = boardService.checkBoardByUser(myPageUserId);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }else {
                DefaultRes<List<MyPageBoard>>defaultRes = boardService.findBoardByUserId(id);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//========================================= 게스트 보드 ===========================================================

    /**
     * 자신의 마이페이지 게스트 보드 조회
     *
     * @param principal
     * @return ResponseEntity
     */
    @GetMapping("/mine/guestboard")
    public ResponseEntity findGuestBoards(final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            DefaultRes<List<GuestModel.GuestModelRes>>defaultRes = myPageService.findGuestBoard(id);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 타인의 마이페이지 게스트 보드 조회
     *
     * @param myPageUserId
     * @param principal
     * @return ResponseEntity
     */
    @GetMapping("others/guestboard/{myPageUserId}")
    public ResponseEntity findGuestBoards(@PathVariable("myPageUserId") final int myPageUserId, final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            if(id == myPageUserId){
                DefaultRes<List<GuestModel.GuestModelRes>>defaultRes = myPageService.checkfindGuestBoard(myPageUserId);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }else {
                DefaultRes<List<GuestModel.GuestModelRes>>defaultRes = myPageService.findGuestBoard(myPageUserId);
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            }
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게스트 보드 저장
     *
     * @param myPageUserId
     * @param guestModelReq
     * @param principal
     * @return
     */

    @PostMapping("/guestboard/{myPageUserId}")
    //@RequestMapping(value = "/guestboard/{myPageUserId}", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity saveGuestBoard(@PathVariable("myPageUserId") final int myPageUserId,
                                         @RequestBody final GuestModel.GuestModelReq guestModelReq, final GingsPrincipal principal) {
        try {
            final int id = principal.getUserId();
            if (id == myPageUserId) {
                DefaultRes defaultRes = myPageService.checkUser();
                return new ResponseEntity<>(defaultRes, HttpStatus.OK);
            } else {
                System.out.println("확인하자 : " + guestModelReq.getContent());
                myPageService.createGuest(guestModelReq, myPageUserId, id);
                int i = guestModelReq.getGuestBoardId();

                String name = myPageService.findByUserId(id).getData().getName();

                String guestboardId = Integer.toString(i);
                String action = "마이페이지";

                String firebaseResponse = fcmService.createFcm(action, myPageUserId, guestboardId, "깅스", name+"님이 게스트 보드를 작성했습니다.");
                //alarmService.insertAlarm(myPageUserId, "mypage", i);
                alarmService.insertAlarm(myPageUserId, id, name+"님이 게스트 보드를 작성했습니다","mypage",i );


                return new ResponseEntity<>(firebaseResponse, HttpStatus.OK);
            }

        }catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //====================================== 설정 설정 설정 설정 설정 설정 ==================================================
 //============================================ 설정 - 자기소개 조회/저장/수정========================================================

    //설정 - 자기소개 조회
    @GetMapping("/setting/introduce")
    public ResponseEntity tryToChangeIntroduce(final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            DefaultRes<List<IntroduceModel.IntroduceRes>> defaultRes = myPageService.selectIntroduce(id);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //설정 - 자기소개 저장/수정
    @PostMapping("setting/introduce")
    public ResponseEntity inputIntroduce(final IntroduceModel.IntroduceReq introduceReq,final GingsPrincipal principal){
        try {
            final int id = principal.getUserId();
            return new ResponseEntity<>(myPageService.saveIntroduce(id, introduceReq), HttpStatus.OK);
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    //===============================프로필 사진 변경====================================================
    //프로필 사진 조회
    @GetMapping("setting/image")
    public ResponseEntity selectProfile(final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            return new ResponseEntity<>(myPageService.selectImg(id), HttpStatus.OK);
        }catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //프로필 사진 저장
    @PutMapping("setting/image")
    public ResponseEntity inputProfile(final MyPage myPage, final GingsPrincipal principal){
        try {
            final int id = principal.getUserId();
            return new ResponseEntity<>(myPageService.saveProfileImg(id, myPage), HttpStatus.OK);
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //===============================프로필 정보 수정====================================================
    //프로필 정보 조회
    @GetMapping("setting/info")
    public ResponseEntity selectProfileInfo(final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            return new ResponseEntity<>(myPageService.selectInformation(id), HttpStatus.OK);
        }catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //프로필 정보 입력
    @PutMapping("setting/info")
    public ResponseEntity createProfileInfo(@RequestBody final MyPage myPage, final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            return new ResponseEntity<>(myPageService.saveInformation(id, myPage), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //프로필 정보 키워드 입력
    @PostMapping("setting/info/keyword")
    public ResponseEntity inputInfoKeyword(@RequestBody final MyPage myPage, final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            return new ResponseEntity<>(myPageService.saveKeyword(id, myPage), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //========================= 원래 비밀번호 체크 ========================================
    @PostMapping("setting/modifyPwd")
    public ResponseEntity checkPassword(@RequestBody final MyPage.MyPagePwdRes myPagePwdRes, final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            return new ResponseEntity<>(myPageService.chkPwd(id, myPagePwdRes), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //=========================== 비밀번호 변경 ===================================================
    @PatchMapping("setting/modifyPwd")
    public ResponseEntity changePwd(@RequestBody final MyPage.MyPagePwdRes myPagePwdRes, final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            return new ResponseEntity<>(myPageService.modifyPwd(id, myPagePwdRes), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
