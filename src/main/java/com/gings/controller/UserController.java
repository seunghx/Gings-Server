package com.gings.controller;

import static com.gings.security.JWTService.AUTHORIZATION;

import java.util.Locale;
import java.util.Random;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.gings.model.DefaultRes;
import com.gings.model.user.SignUp;
import com.gings.model.user.SignUp.EmailReq;
import com.gings.security.AuthNumberTokenInfo;
import com.gings.security.JWTServiceManager;
import com.gings.security.TokenInfo;
import com.gings.security.utils.AuthenticationNumberNotificationProvider;
import com.gings.service.UserService;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller("/user")
public class UserController {
    
    private static final Class<? extends TokenInfo> USING_TOKEN_INFO = AuthNumberTokenInfo.class;
    
    private final UserService userService;
    private final MessageSource msgSource;
    private final JWTServiceManager jwtServiceManager;
    private final AuthenticationNumberNotificationProvider notificationProvider;
    
    public UserController(UserService userService, MessageSource msgSource, 
                          JWTServiceManager jwtServiceManager, 
                          AuthenticationNumberNotificationProvider notificationProvider) {
        this.userService = userService;
        this.msgSource = msgSource;
        this.jwtServiceManager = jwtServiceManager;
        this.notificationProvider = notificationProvider;
    }
    
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<DefaultRes<Void>> onUserEmailDuplicated(DuplicateKeyException e, 
                                                                  WebRequest request) {
        log.error("Request email duplicated.", e);
        
        String message = msgSource.getMessage("response.email-duplicate", null, request.getLocale());
        
        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.CONFLICT.value(), message), 
                                    HttpStatus.OK);
    }
    
    /**
     * 요청으로 전달한 이메일이 존재하지 않음의 의미로 404 Not Found 응답 반환.
     * 존재할 경우 204 No Content 반환.
     * 
     * (일반적으로 200 OK는 GET 요청의 경우 바디에 응답 데이터가 포함되는 경우이므로)
     */
    @GetMapping("/email")
    public ResponseEntity<DefaultRes<Void>> checkEmailDuplication(String email, Locale locale) {
        if(userService.isEmailExist(email)) {
            log.info("Requested email {} aleady exists.", email);
            
            String message = msgSource.getMessage("response.email-duplicate", null, locale);
            
            return new ResponseEntity<>(new DefaultRes<>(HttpStatus.NO_CONTENT.value(), message), 
                                        HttpStatus.OK);
        }else {
            log.info("Requested email {} does not exist.");
            
            String message = msgSource.getMessage("response.email-not-duplicate", null, locale);
            
            return new ResponseEntity<>(new DefaultRes<>(HttpStatus.NOT_FOUND.value(), message), 
                                        HttpStatus.OK);
        }
    }
    
    @GetMapping("/authNumber")
    public ResponseEntity<DefaultRes<Void>> getAuthenticationNubmer(@Validated EmailReq emailReq,
                                                                    Locale locale) {
        
        String authNumber = getAuthenticationNumber();
        
        setAuthToken(authNumber);
        notificationProvider.sendAuthenticationNumber(emailReq.getEmail(), authNumber);
        
        String message = msgSource.getMessage("response.auth-number.succees", null, locale);
        
        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.OK.value(), message),
                                    HttpStatus.OK);
    }
    
    @PostMapping("/signup")
    public ResponseEntity<DefaultRes<Void>> signup(@Validated SignUp signUp) {
        verifyAuthenticationNumber(signUp.getAuthNumber());
        
        return null;
    }
    
    private void verifyAuthenticationNumber(String authNumber) {
        
    }
    
    /**
     * @return 인증을 위해 사용자에게 전달 될 네 자리의 인증 번호
     */
    private String getAuthenticationNumber() {
       Random random = new Random();
       
       return String.valueOf(random.nextInt(9000) + 1000);
    }
    
    /**
     * @param authNumber jwt token에 저장될 인증 번호.
     */
    private void setAuthToken(String authNumber) {
        AuthNumberTokenInfo tokenInfo = new AuthNumberTokenInfo();
        tokenInfo.setAuthNumber(authNumber);
        
        String jwt =  jwtServiceManager.resolve(USING_TOKEN_INFO).create(tokenInfo);
        
        ServletWebRequest servletContainer =
                        (ServletWebRequest)RequestContextHolder.getRequestAttributes();

        servletContainer.getResponse()
                        .setHeader(AUTHORIZATION, jwt);
        
    }
    
}
