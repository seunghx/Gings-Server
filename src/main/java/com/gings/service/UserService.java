package com.gings.service;

import com.gings.dao.UserMapper;
import com.gings.domain.Introduce;
import com.gings.domain.Signature;
import com.gings.domain.User;
import com.gings.domain.UserKeyword;
import com.gings.model.DefaultRes;
import com.gings.model.user.SignUp;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

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

    public boolean isEmailExist(String email) {
        int count = userMapper.countByEmail(email);
        
        if(count == 0) {            
            return false;
        }else {
            return true;
        }
    }


}
