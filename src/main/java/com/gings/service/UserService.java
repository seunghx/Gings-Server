package com.gings.service;

import com.gings.dao.UserMapper;
import com.gings.domain.Introduce;
import com.gings.domain.Signature;
import com.gings.domain.User;
import com.gings.domain.UserKeyword;
import com.gings.model.DefaultRes;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;

import java.util.List;

public class UserService {
    private final UserMapper userMapper;

    /**
     * 생성자 의존성 주입
     *
     * @param userMapper
     */
    public UserService(final UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    /**
     * 회원 고유 번호로 회원 조회
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findByUserId(final int id) {
        final User user = userMapper.findByUserId(id);
        if (user == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, user);
    }

    /**
     * 회원 고유 번호로 회원 자기 소개 조회
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findIntroduceByUserId(final int id) {
        final List<Introduce> introduces = userMapper.findIntroduceByUserId(id);
        if(introduces == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, introduces);
    }

    /**
     * 회원 고유 번호로 회원 키워드 조회
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findKeywordsByUserId(final int id) {
        final List<UserKeyword> keywords = userMapper.findKeywordsByUserId(id);
        if(keywords == null)
           return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, keywords);
    }

    /**
     * 회원 고유 번호로 회원 시그니처 보드 조회
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findSignaturesByUserId(final int id) {
        final List<Signature> signatures = userMapper.findSignaturesByUserId(id);
        if(signatures == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, signatures);
    }

    /**
     * 회원 자기소개 고유 번호로 회원 이미지 조회
     *
     * @param id 회원 고유 번호
     * @return DefaultRes
     */
    public DefaultRes findImagesByIntroduceId(final int id) {
        final List<String> images = userMapper.findImagesByIntroduceId(id);
        for(String image : images){
            if(image == null || image.isEmpty())
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
        }
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, images);
    }
}
