package com.gings.controller;

import com.gings.dao.UserMapper;
import com.gings.model.DefaultRes;
import com.gings.model.user.Login.LoginReq;
import com.gings.model.user.Login.LoginRes;
import com.gings.security.jwt.JWTService;
import com.gings.security.jwt.JWTServiceManager;
import com.gings.security.jwt.TokenInfo;
import com.gings.security.jwt.UserAuthTokenInfo;
import com.gings.utils.code.UserRole;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import static com.gings.security.jwt.JWTService.AUTHORIZATION;
import static com.gings.security.jwt.JWTService.BEARER_SCHEME;
import static com.gings.utils.ResponseMessage.LOGIN_SUCCESS;

import java.util.Optional;



/**
 *
 * login 요청 처리 핸들러.
 * 
 * (login과 같은 인증 로직은 후에 따로 security단으로 분리하기 위해 로그인 전용으로 이 클래스를 따로 정의하였음.)
 * 
 * 
 * @author seunghyun
 *
 */
@Slf4j
@RestController
public class LoginController {

    private static final Class<? extends TokenInfo> USING_TOKEN_INFO = UserAuthTokenInfo.class;

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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<DefaultRes<Void>> onUsernameNotFound(UsernameNotFoundException ex, WebRequest request) {

        log.error("Exception occurred while trying to log in user. Exception : ", ex);

        String message = msgSource.getMessage("response.authentication.invalid-email-password",
                null, request.getLocale());

        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.UNAUTHORIZED.value(), message),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DefaultRes<Void>> onBadCredentials(BadCredentialsException ex, WebRequest request) {
        log.error("Exception occurred while trying to log in user. Exception : ", ex);

        String message = msgSource.getMessage("response.authentication.invalid-email-password",
                null, request.getLocale());

        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.UNAUTHORIZED.value(), message),
                HttpStatus.UNAUTHORIZED);
    }

    /**
     * login 수행
     */
    @PostMapping("/login")
    public ResponseEntity<DefaultRes<LoginRes>> login(@Validated @RequestBody final LoginReq loginReq) throws Throwable {

        LoginUser user =
                Optional.ofNullable(userMapper.findByEmail(loginReq.getEmail()))
                        .orElseThrow(() -> {

                            if (log.isInfoEnabled()) {
                                log.info("Login failed for user email : {}", loginReq.getEmail());
                            }

                            throw new UsernameNotFoundException("Invalid user email.");
                        });

        return loginInternal(loginReq, user);

    }

    /**
     * password 비교 및 user email 인증 확인.
     */
    private ResponseEntity<DefaultRes<LoginRes>> loginInternal(LoginReq req, LoginUser user) {

        String email = req.getEmail();

        if (passwordEncoder.matches(req.getPwd(), user.getPwd())) {

            log.info("Login succeeded for user email : {}", email);
            
            userMapper.findByEmail(email);
            
            userMapper.saveFcmToken(user.getUserId(), req.getFcm());

            return new ResponseEntity<>(new DefaultRes<>(HttpStatus.CREATED.value(), LOGIN_SUCCESS,
                    onLoginSuccess(user)),
                    HttpStatus.OK);
        } else {
            log.info("Login failed because of invalid password for user email : {}", email);

            throw new BadCredentialsException("Invalid password for user :" + email);
        }
    }

    /**
     * 
     * 사정이 생겨 우선 JWT token은 헤더와 바디에 모두 넘기고 후에 변경 예정.
     * 
     * @param user
     */
    private LoginRes onLoginSuccess(LoginUser user){
        setTokenToResponse(user);

        TokenInfo tokenInfo = new UserAuthTokenInfo(user.getUserId(), user.getRole());
        
        JWTService jwtService = jwtServiceManager.resolve(USING_TOKEN_INFO);
        String jwt = BEARER_SCHEME + jwtService.create(tokenInfo);
        
        if(user.isFirstLogin()) {
            userMapper.setFalseToFirstLogin(user.getUserId());
        }
        
        return new LoginRes(user.getUserId(), user.firstLogin, jwt);
    }
    

    private void setTokenToResponse(LoginUser user) {
        TokenInfo tokenInfo = new UserAuthTokenInfo(user.getUserId(), user.getRole());

        JWTService jwtService = jwtServiceManager.resolve(USING_TOKEN_INFO);
        String jwt = BEARER_SCHEME + jwtService.create(tokenInfo);

        ServletRequestAttributes requestAttr = (ServletRequestAttributes)
                                            RequestContextHolder.getRequestAttributes();

        requestAttr.getResponse().setHeader(AUTHORIZATION, jwt);
    }

    @Setter
    @Getter
    @ToString
    public static class LoginUser {
        private int userId;
        private UserRole role;
        private String pwd;
        private boolean firstLogin;
    }
}