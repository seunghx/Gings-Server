package com.gings.controller;

import com.gings.dao.UserMapper;
import com.gings.model.ApiError;
import com.gings.model.DefaultRes;
import com.gings.model.LoginReq;
import com.gings.security.DefaultJWTService;
import com.gings.security.JWTService;
import com.gings.security.JWTServiceManager;
import com.gings.security.TokenInfo;
import com.gings.security.UserAuthTokenInfo;
import com.gings.security.authentication.AuthAspect;
import com.gings.utils.code.UserRole;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;
import static com.gings.utils.ResponseMessage.LOGIN_SUCCESS;
import static com.gings.utils.ResponseMessage.LOGIN_FAIL;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class LoginController {
    
    private static final Class<? extends TokenInfo> USING_TOKEN_INFO = UserAuthTokenInfo.class;
    private static final DefaultRes loginSuccessRes = 
                            new DefaultRes(HttpStatus.CREATED.value(), LOGIN_SUCCESS);
    private static final DefaultRes loginFailedRes = 
                        new DefaultRes(HttpStatus.UNAUTHORIZED.value(), LOGIN_FAIL);

    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTServiceManager jwtServiceManager;
    private final MessageSource msgSource;
    
    public LoginController(UserMapper userMapper, PasswordEncoder passwordEncoder, 
                           JWTServiceManager jwtServiceManager, MessageSource msgSource) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtServiceManager = jwtServiceManager;
        this.msgSource = msgSource;
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<DefaultRes<Void>> login(@RequestBody final LoginReq loginReq, 
                                                  HttpServletRequest request, 
                                                  HttpServletResponse response) {
                
        LoginUser loginUser = userMapper.findByEmail(loginReq.getEmail());
                                      
        
        if(loginUser == null) {
            if(log.isInfoEnabled()) {
                log.info("Login failed for user email : {}", loginReq.getEmail());
            }
            return new ResponseEntity<>(loginFailedRes, HttpStatus.UNAUTHORIZED);
        }
        
        if(passwordEncoder.matches(loginReq.getPwd(), loginUser.getPwd())){
            TokenInfo tokenInfo = new UserAuthTokenInfo(loginUser.getUserId(), loginUser.getRole());
            
            JWTService jwtService = jwtServiceManager.resolve(USING_TOKEN_INFO);
            String jwt = AuthAspect.BEARER_SCHEME + jwtService.create(tokenInfo);
            
            response.setHeader(AuthAspect.AUTHORIZATION, jwt);
            return new ResponseEntity<>(loginSuccessRes, HttpStatus.CREATED);
        }else {
            if(log.isInfoEnabled()) {
                log.info("Login failed because of invalid password for user email : {}"
                                                                                , loginReq.getEmail());
            }
            
            return new ResponseEntity<>(loginFailedRes, HttpStatus.UNAUTHORIZED);
        }
    }
    
    @Setter
    @Getter
    public static class LoginUser {
        private int userId;
        private UserRole role;
        private String pwd;
    }
}
