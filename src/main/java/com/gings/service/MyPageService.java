package com.gings.service;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;
import com.gings.domain.Introduce;
import com.gings.model.DefaultRes;
import com.gings.model.GuestModel;
import com.gings.model.IntroduceModel;
import com.gings.model.MyPage;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class MyPageService implements ApplicationEventPublisherAware{
   
    private final BoardMapper boardMapper;
    private final UserMapper userMapper;
    private final S3MultipartService s3MultipartService;

    private ApplicationEventPublisher eventPublisher;

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
     * 게스트 보드 작성
     *
     * @param guestModelReq 객체
     * @return DefaultRes
     */
    public DefaultRes createGuest(final GuestModel.GuestModelReq guestModelReq, final int myPageUserId, final int id){
        try{
            userMapper.saveGuest(guestModelReq, myPageUserId, id);
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
     * 게스트 보드 출력
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

    //================================설정 - 자기소개 출력/저장/수정================================================
    /**
     * 설정 유저 자기소개 출력
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<List<IntroduceModel.IntroduceRes>> selectIntroduce(final int id){
        final List<IntroduceModel.IntroduceRes> introduceRes = userMapper.selectIntroBeforeChange(id);
        if(introduceRes.isEmpty())
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_INTRODUCE);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.YES_INTRODUCE, introduceRes);
    }

    /**
     * 설정 유저 자기소개 저장
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes<IntroduceModel.IntroduceReq> saveIntroduce(final int id, IntroduceModel.IntroduceReq introduceReq){
        try{
            userMapper.saveIntroByUserId(id, introduceReq);
            final int introduceId = introduceReq.getId();
            System.out.println(introduceId);

            List<String> urlList = s3MultipartService.uploadMultipleFiles(introduceReq.getImages());

            userMapper.saveIntroduceImg(introduceId, urlList);

            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATE_INTRODUCE);

        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.FAILED_TO_CREATE_INTRODUCE);
        }
    }


    /**
     *
     *
     * @param id
     * @param introduceReq
     * @return DefaultRes
     */
    public DefaultRes changeUserIntroduce(final int id, IntroduceModel.IntroduceReq introduceReq){
        try{
            userMapper.updateIntroduce(id, introduceReq);
            final int introduceId = introduceReq.getId();

            System.out.println(introduceId);
            s3MultipartService.deleteMultipleFiles(introduceReq.getPrevImagesUrl());
            //userMapper.deleteIntroduceImg(introduceId);

            for(String url : introduceReq.getPrevImagesUrl()){
                userMapper.deleteIntroduceImg(url);
            }
            userMapper.updateIntroduceImg(introduceId, introduceReq.getPostImagesUrl());
            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.UPDATED_INTRODUCE);
        } catch (Exception e){
            log.info(e.getMessage());
            return DefaultRes.res(StatusCode.FAILED, ResponseMessage.FAILED_UPDATING_INTRODUCE);
        }

    }

    /**
     * 프로필 사진 조회
     *
     * @param id
     * @return DefaultRes
     */
    public DefaultRes selectImg(final int id){
        final MyPage.MyPageProfile profile = userMapper.selectProfileImg(id);
        if(profile == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.CANT_FIND_PROFILEIMG);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.YES_PROFILEIMG, profile);
    }

    /**
     * 프로필 사진 저장 및 업데이트
     *
     * @param id
     * @param myPage
     * @return DefaultRes
     */
    public DefaultRes saveProfileImg(final int id, MyPage myPage){
        try{
            s3MultipartService.deleteSingleFile(myPage.getImage());

            String url = s3MultipartService.uploadSingleFile(myPage.getImgFile());
            userMapper.updateProfileImg(id, url);
            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATED_PROFILE_IMG);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.FAILED_TO_CREATE_PROFILE_IMG);
        }
    }

    /**
     * 프로필 정보 조회
     *
     * @param id
     * @return DefaultRes
     */
    public DefaultRes selectInformation(final int id){
        final MyPage myPage = userMapper.findByUserId2(id);
        if(myPage == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.CANT_FIND_PROFILE_INFO);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.YES_PROFILE_INFO, myPage);
    }

    /**
     * 프로필 정보 저장
     *
     * @param id
     * @param myPage
     * @return DefaultRes
     */
    public DefaultRes saveInformation(final int id, MyPage myPage){
        try{
            userMapper.saveProfileInfo(id, myPage);
            final int userId = myPage.getId();
            //userMapper.saveProfileKeyword(userId, myPage.getKeywords());
            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATED_PROFILE_INFO);
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.FAILED_TO_CREATE_PROFILE_INFO);
        }
    }

    /**
     * 프로필 정보 키워드 저장
     *
     * @param id
     * @param myPage
     * @return DefaultRes
     */
    public DefaultRes saveKeyword(final int id, MyPage myPage){
        try{
            userMapper.deleteKeyword(id);
            userMapper.saveProfileKeyword(id, myPage.getKeywords());
            return DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATED_PROFILE_KEYWORD);
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.FAILED_TO_CREATE_PROFILE_KEYWORD);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        
    }

}
