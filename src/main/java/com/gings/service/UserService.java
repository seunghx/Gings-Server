package com.gings.service;

import com.gings.dao.UserMapper;
import com.gings.domain.Introduce;
import com.gings.domain.Signature;
import com.gings.domain.User;
import com.gings.domain.UserKeyword;
import com.gings.model.DefaultRes;
import com.gings.model.Pagination;
import com.gings.model.board.HomeBoard;
import com.gings.model.user.SignUp;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.omg.CORBA.portable.ValueOutputStream;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 생성자 의존성 주입
     *
     * @param userMapper
     */
    public UserService(final UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    public void addNewUser(SignUp signUp) {
        if(signUp == null) {
            log.info("Received null valued signUp parameter.");
            throw new IllegalArgumentException("Null valued signup parameter");
        }
        
        signUp.setPwd(passwordEncoder.encode(signUp.getPwd()));
        
        userMapper.save(signUp);
    }

    public DefaultRes<Void> deleteUser(final int userId) {
        if(userMapper.findByUserId(userId) == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);

        userMapper.deleteUser(userId);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.DELETE_USER);
    }

    public boolean isEmailExist(String email) {
        int count = userMapper.countByEmail(email);
        
        if(count == 0) {            
            return false;
        }else {
            return true;
        }
    }


}
