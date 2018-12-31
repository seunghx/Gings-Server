package com.gings.controller;

import com.gings.dao.UserMapper;
import com.gings.model.DefaultRes;
import com.gings.model.Login.LoginReq;
import com.gings.model.Login.LoginRes;
import com.gings.security.JWTService;
import com.gings.security.JWTServiceManager;
import com.gings.security.TokenInfo;
import com.gings.security.UserAuthTokenInfo;
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
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static com.gings.utils.ResponseMessage.LOGIN_SUCCESS;

import java.util.Optional;

import static com.gings.security.JWTService.AUTHORIZATION;
import static com.gings.security.JWTService.BEARER_SCHEME;



/**
 *
 * login 요청 처리 핸들러.
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

    @ExceptionHandler(EmailNotConfirmedException.class)
    public ResponseEntity<DefaultRes<Void>> onEmailNotConfirmed(EmailNotConfirmedException ex, WebRequest request) {
        log.error("Exception occurred while trying to log in user. Exception : ", ex);

        String message = msgSource.getMessage("response.authentication.email-not-authenticated",
                new String[] {ex.getEmail()}, request.getLocale());

        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.FORBIDDEN.value(), message),
                HttpStatus.FORBIDDEN);
    }

    /**
     * login 수행
     */
    @PostMapping("/login")
    public ResponseEntity<DefaultRes<LoginRes>> login(@Validated @RequestBody final LoginReq loginReq) throws Throwable {

        LoginUser user =
                 Optional.ofNullable(userMapper.findByEmail(loginReq.getEmail()))
                        .orElseThrow(() -> {

                            if(log.isInfoEnabled()) {
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

        if(passwordEncoder.matches(req.getPwd(), user.getPwd())){

            if(!user.isEmailConfirmed()) {
                log.info("Login failed because user email : {} does not authenticated yet.", email);

                throw new EmailNotConfirmedException("User email not confirmed.", email);
            }

            log.info("Login succeeded for user email : {}", email);

            return new ResponseEntity<>(new DefaultRes<>(HttpStatus.CREATED.value(), LOGIN_SUCCESS,
                    onLoginSuccess(user)),
                    HttpStatus.CREATED);
        }else {
            log.info("Login failed because of invalid password for user email : {}", email);

            throw new BadCredentialsException("Invalid password for user :" + email);
        }
    }

    private LoginRes onLoginSuccess(LoginUser user){

        TokenInfo tokenInfo = new UserAuthTokenInfo(user.getUserId(), user.getRole());

        JWTService jwtService = jwtServiceManager.resolve(USING_TOKEN_INFO);
        String jwt = BEARER_SCHEME + jwtService.create(tokenInfo);

        ServletWebRequest servletContainer =
                (ServletWebRequest)RequestContextHolder.getRequestAttributes();

        servletContainer.getResponse()
                .setHeader(AUTHORIZATION, jwt);

        return new LoginRes(user.firstLogin);
    }

    private class EmailNotConfirmedException extends RuntimeException {

        private static final long serialVersionUID = 276919101137697050L;

        private final String email;

        EmailNotConfirmedException(String message, String email) {
            super(message);
            this.email = email;
        }

        String getEmail() {
            return email;
        }
    }

    @Setter
    @Getter
    @ToString
    public static class LoginUser {
        private int userId;
        private UserRole role;
        private String pwd;
        private boolean emailConfirmed;
        private boolean firstLogin;
    }
}