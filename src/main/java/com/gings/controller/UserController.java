package com.gings.controller;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;
import static com.gings.security.jwt.JWTService.AUTHORIZATION;
import static com.gings.security.jwt.JWTService.BEARER_SCHEME;

import java.util.Locale;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;
import com.gings.security.GingsPrincipal;
import com.gings.security.authentication.Authentication;
import org.apache.ibatis.annotations.Delete;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gings.model.DefaultRes;
import com.gings.model.user.SignUp;
import com.gings.model.user.SignUp.EmailReq;
import com.gings.security.jwt.EmailAuthTokenInfo;
import com.gings.security.jwt.JWTServiceManager;
import com.gings.security.jwt.TokenInfo;
import com.gings.security.utils.AuthenticationNumberNotificationProvider;
import com.gings.service.UserService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UserController {
    
    private static final String XFF_HEADER_NAME = "X-Forwarded-For";
    private static final Class<? extends TokenInfo> USING_TOKEN_INFO = EmailAuthTokenInfo.class;
    
    private final UserService userService;
    private final BoardMapper boardMapper;
    private final MessageSource msgSource;
    private final JWTServiceManager jwtServiceManager;
    private final AuthenticationNumberNotificationProvider notificationProvider;
    
    public UserController(UserService userService, BoardMapper boardMapper,
                          MessageSource msgSource,
                          JWTServiceManager jwtServiceManager,
                          AuthenticationNumberNotificationProvider notificationProvider) {
        
        this.userService = userService;
        this.boardMapper = boardMapper;
        this.msgSource = msgSource;
        this.jwtServiceManager = jwtServiceManager;
        this.notificationProvider = notificationProvider;
    }
    
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<DefaultRes<Void>> onUserEmailDuplicated(DuplicateKeyException e, 
                                                                  WebRequest request) {
        
        String remote =  getRequestedAddr();
        
        log.error("Request email duplicated. {}", e);
        log.warn("It might be illegal access!! Requesting remote host : {}", remote);
                  
        String message = msgSource.getMessage("response.email-duplicate", null, request.getLocale());
        
        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.CONFLICT.value(), message), 
                                    HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<DefaultRes<Void>> onEmailAuthTokenExpired(TokenExpiredException e,                      
                                                                     WebRequest request) {
        log.info("Email authentication request token expired.");
        
        String message = msgSource.getMessage("response.email-auth-token.expired", null, request.getLocale());
        
        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.UNAUTHORIZED.value(), message),
                                    HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<DefaultRes<Void>> onJWTVerificationException(JWTVerificationException e, 
                                                                       WebRequest request) {

        String remote =  getRequestedAddr();
        
        log.error("Exception occurred while trying to authenticate user request.", e);
        log.warn("It might be illegal access!! Requesting remote user ip : {}", remote);
    

        String message = msgSource.getMessage("response.authentication.failure", null, request.getLocale());
        
        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.UNAUTHORIZED.value(), message), 
                                    HttpStatus.UNAUTHORIZED);
    }
                                                                           
    /**
     * 요청으로 전달한 이메일이 존재하지 않음의 의미로 404 Not Found 응답 반환.
     * 존재할 경우 204 No Content 반환.
     * 
     * (일반적으로 200 OK는 GET 요청의 경우 바디에 응답 데이터가 포함되는 경우이므로)
     */
    @GetMapping("/signup/email")
    public ResponseEntity<DefaultRes<Void>> checkEmailDuplication(@Validated EmailReq emailReq, 
                                                                  Locale locale) {
        String email = emailReq.getEmail();
        
        if(userService.isEmailExist(email)) {
            log.info("Requested email {} aleady exists.", email);
            
            String message = msgSource.getMessage("response.email-duplicate", null, locale);
            
            return new ResponseEntity<>(new DefaultRes<>(HttpStatus.NO_CONTENT.value(), message), 
                                        HttpStatus.OK);
        } else {
            log.info("Requested email {} does not exist.", email);
            
            String message = msgSource.getMessage("response.email-not-duplicate", null, locale);
            
            return new ResponseEntity<>(new DefaultRes<>(HttpStatus.NOT_FOUND.value(), message), 
                                        HttpStatus.OK);
        }
    }
    /*
    @GetMapping("/signup/authNumber")
    public ResponseEntity<DefaultRes<Void>> 
                        getAuthenticationNubmer(@Validated EmailReq emailReq,
                                                Locale locale) {
        
        String authNumber = getAuthenticationNumber();
        
        setAuthToken(authNumber, emailReq.getEmail());
        notificationProvider.sendAuthenticationNumber(emailReq.getEmail(), authNumber);
        
        String message = msgSource.getMessage("response.auth-number.succees", null, locale);
        
        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.OK.value(), message),
                                    HttpStatus.OK);
    }*/
    
    @GetMapping("/signup/authNumber")
    public ResponseEntity<DefaultRes<Temp>> 
                        getAuthenticationNubmer(@Validated EmailReq emailReq,
                                                Locale locale) {
        
        String authNumber = getAuthenticationNumber();
        
        String jwt = setAuthToken(authNumber, emailReq.getEmail());
        notificationProvider.sendAuthenticationNumber(emailReq.getEmail(), authNumber);
        
        String message = msgSource.getMessage("response.auth-number.succees", null, locale);
        
        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.OK.value(), message, new Temp(jwt)),
                                    HttpStatus.OK);
    }
    
    @PostMapping("/signup")
    public ResponseEntity<DefaultRes<Void>> signup(@Validated @RequestBody SignUp signUp, 
                                                   HttpServletRequest request) {
        
        String jwt = resolveJWTToken(request);
        
        EmailAuthTokenInfo tokenInfo = 
                    (EmailAuthTokenInfo)getEmailFromToken(signUp.getAuthNumber(), jwt);
        
        signUp.setEmail(tokenInfo.getEmail());
        userService.addNewUser(signUp);
        
        String message = msgSource.getMessage("response.sign-up.success", null, request.getLocale());
        
        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.CREATED.value(), message), 
                                    HttpStatus.OK);
    }
    
    @PostMapping("/signup/minyoung")
    public ResponseEntity<DefaultRes<Void>> signupMinyoung(@RequestBody SignUp signUp,
                                                           Locale locale) {
        userService.addNewUser(signUp);
        
        String message = msgSource.getMessage("response.sign-up.success", null, locale);
        
        return new ResponseEntity<>(new DefaultRes<>(HttpStatus.CREATED.value(), message), 
                                    HttpStatus.OK);
    }
    @Authentication
    @DeleteMapping("/deleteAccount")
    public ResponseEntity<DefaultRes<Void>> deleteAccount(final GingsPrincipal principal) {
        try {
            final int userId = principal.getUserId();
            return new ResponseEntity<>(userService.deleteUser(userId),  HttpStatus.OK);
        } catch (Exception e) {
            log.error("{}", e);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.NOT_FOUND);
        }
    }
    
    private String resolveJWTToken(HttpServletRequest request) {

        String jwt = request.getHeader(AUTHORIZATION);
        if(StringUtils.isEmpty(jwt)) {
            log.warn("Illegal request. JWT token is null.");
            
            throw new BadCredentialsException("JWT token is null");
        }
        
        if(!jwt.startsWith(BEARER_SCHEME)) {
            log.warn("Illegal request. Invalid JWT token detected. jwt : {}", jwt);
            
            throw new BadCredentialsException("JWT token does not start with " + BEARER_SCHEME);
        }
        
        return jwt.replace(BEARER_SCHEME, "");
    }

    private TokenInfo getEmailFromToken(String authNumber, String jwt) {
        
        
        EmailAuthTokenInfo tokenInfo = new EmailAuthTokenInfo(jwt, authNumber);
        
        if(StringUtils.isEmpty(jwt)) {
            log.info("Received invalid empty jwt token.");
            
            throw new BadCredentialsException("JWT token including authentication number is empty.");
        }
        
        return jwtServiceManager.resolve(USING_TOKEN_INFO)
                                .decode(tokenInfo);
    }
    
    /**
     * @return 인증을 위해 사용자에게 전달 될 네 자리의 인증 번호
     */
    private String getAuthenticationNumber() {
       Random random = new Random();
       
       return String.valueOf(random.nextInt(9000) + 1000);
    }
    
    /*
    private void setAuthToken(String authNumber, String email) {
        
        EmailAuthTokenInfo tokenInfo = new EmailAuthTokenInfo();
        tokenInfo.setAuthNumber(authNumber);
        tokenInfo.setEmail(email);
        
        String jwt =  jwtServiceManager.resolve(USING_TOKEN_INFO)
                                       .create(tokenInfo);
        
        ServletRequestAttributes requestAttr = (ServletRequestAttributes)
                                               RequestContextHolder.getRequestAttributes();

        requestAttr.getResponse()
                   .setHeader(AUTHORIZATION, BEARER_SCHEME + jwt);
    }
    */
    
    private String setAuthToken(String authNumber, String email) {
        
        EmailAuthTokenInfo tokenInfo = new EmailAuthTokenInfo();
        tokenInfo.setAuthNumber(authNumber);
        tokenInfo.setEmail(email);
        
        String jwt =  jwtServiceManager.resolve(USING_TOKEN_INFO)
                                       .create(tokenInfo);
        
        ServletRequestAttributes requestAttr = (ServletRequestAttributes)
                                               RequestContextHolder.getRequestAttributes();

        requestAttr.getResponse()
                   .setHeader(AUTHORIZATION, BEARER_SCHEME + jwt);
        
        return BEARER_SCHEME + jwt;
    }
    
    private String getRequestedAddr() {
        HttpServletRequest request = 
                ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
                                                               .getRequest();
        
        String remote = request.getHeader(XFF_HEADER_NAME);
        
        if(remote == null) {
            remote = request.getRemoteAddr();
        }
        
        return remote;
    }
    
    @Getter
    @AllArgsConstructor
    public static class Temp {
        private String jwt;
    }
}
