package com.gings.service;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;
import com.gings.model.DefaultRes;
import com.gings.model.GuestModel;
import com.gings.model.IntroduceModel;
import com.gings.model.MyPage;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class MyPageService {
    private final BoardMapper boardMapper;
    private final UserMapper userMapper;
    private final S3MultipartService s3MultipartService;


    public MyPageService(BoardMapper boardMapper, UserMapper userMapper, S3MultipartService s3MultipartService) {
        this.boardMapper = boardMapper;
        this.userMapper = userMapper;
        this.s3MultipartService = s3MultipartService;
    }

    /**
     * 마이페이지 유저 고유 번호로 정보 출력
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<MyPage>findByUserId(final int id) {
        final MyPage myPage = userMapper.findByUserId2(id);
        if (myPage == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, myPage);
    }
    public DefaultRes<MyPage> checkUserId(final int id){
        final MyPage myPage = userMapper.findByUserId2(id);
        if (myPage == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.UNQUALIFIED, myPage);
    }

    /**
     * 마이페이지 유저 자기소개 출력
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<MyPage.MyPageIntro> userIntroduce(final int id){
        final MyPage.MyPageIntro myPage = userMapper.findUserIntro(id);
        if(myPage == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_INTRODUCE);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.YES_INTRODUCE, myPage);
    }
    public DefaultRes<MyPage.MyPageIntro> checkuserIntroduce(final int id){
        final MyPage.MyPageIntro myPage = userMapper.findUserIntro(id);
        if(myPage == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_INTRODUCE);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.UNQUALIFIED, myPage);
    }

    /**
     * 시그니처 보드 작성
     *
     * @param guestModelReq 객체
     * @return DefaultRes
     */
    public DefaultRes createGuest(final GuestModel.GuestModelReq guestModelReq, final int id){
        try{
            userMapper.saveGuest(guestModelReq, id);
            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATE_GUESTBOARD);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.FAILED_TO_CREATE_GUESTBOARD);
        }
    }
    public DefaultRes checkUser(){
        return DefaultRes.res(StatusCode.OK, ResponseMessage.UNQUALIFIED);
    }


    /**
     * 시그니처 보드 출력
     *
     * @param id 유저 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<List<GuestModel.GuestModelRes>> findGuestBoard(final int id){
        try{
            final List<GuestModel.GuestModelRes> guestModelRes = userMapper.findGuestBoardByUserId(id);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_GUESTBOARD, guestModelRes);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.FAILED_TO_GET_GUESTBOARD);
        }
    }



    public DefaultRes<List<GuestModel.GuestModelRes>>checkfindGuestBoard(final int id){
        try{
            final List<GuestModel.GuestModelRes> guestModelRes = userMapper.findGuestBoardByUserId(id);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.UNQUALIFIED, guestModelRes);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.FAILED_TO_GET_GUESTBOARD);
        }
    }

    /**
     * 마이페이지 유저 자기소개 출력
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<IntroduceModel.IntroduceRes> selectIntroduce(final int id){
        final IntroduceModel.IntroduceRes myPage = userMapper.selectIntroBeforeChange(id);
        if(myPage == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_INTRODUCE);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.YES_INTRODUCE, myPage);
    }


    public DefaultRes changeUserIntroduce(final int id, IntroduceModel.IntroduceReq introduceReq){
        try{
            userMapper.updateIntroduce(id, introduceReq);
            final int introduceId = introduceReq.getId();

            System.out.println(introduceId);

            //userMapper.deleteIntroduceImg(introduceId);
            final List<String> sampleUrl = new LinkedList<>();

            List<Integer> indexOfPrevImages=introduceReq.getIndexOfPrevImages();
            List<MultipartFile> images = introduceReq.getImages();
            for(int i : indexOfPrevImages){ userMapper.deleteIntroduceImg(i); }

            for(MultipartFile image : images ){
                // S3 저장
                userMapper.updateIntroduceImg(introduceId, image);
            }



            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.UPDATED_INTRODUCE);
        } catch (Exception e){
            log.info(e.getMessage());
            return DefaultRes.res(StatusCode.FAILED, ResponseMessage.FAILED_UPDATING_INTRODUCE);
        }

    }

}
