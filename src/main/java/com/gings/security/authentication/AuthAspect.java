package com.gings.security.authentication;

import static com.gings.security.jwt.JWTService.AUTHORIZATION;
import static com.gings.security.jwt.JWTService.BEARER_SCHEME;

import java.util.Arrays;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;

import com.gings.model.ApiError;
import com.gings.security.GingsPrincipal;
import com.gings.security.jwt.JWTService;
import com.gings.security.jwt.JWTServiceManager;
import com.gings.security.jwt.UserAuthTokenInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author seunghyun
 *
 */
@Slf4j
@Component
@Order(1)
@Aspect
public class AuthAspect {
 
    public static final String XFF_HEADER_NAME = "X-FORWARDED-FOR";
    public static final String USER_ID= "userId";
    public static final String USER_ROLE = "role";
    
    private static final String UNAUTHORIZED_MSG = "response.authentication.failure";
    
    private static final Class<? extends UserAuthTokenInfo> USING_TOKEN_INFO  = UserAuthTokenInfo.class;
    
    /**
     * 인증 실패 시 기본 반환 Response
     */
    private static ResponseEntity<ApiError> AUTH_FAILURE_RES;

    private final HttpServletRequest httpServletRequest;
    private final JWTServiceManager jwtServiceManager;
    private final MessageSource msgSource;

    public AuthAspect(HttpServletRequest httpServletRequest, JWTServiceManager jwtServiceManager,
                      MessageSource msgSource) {
        
        this.httpServletRequest = httpServletRequest;
        this.jwtServiceManager = jwtServiceManager;
        this.msgSource = msgSource;
        
    }
    
    @PostConstruct
    public void init() {
        ApiError authFailRES = new ApiError(HttpStatus.UNAUTHORIZED.value(),
                                               msgSource.getMessage(UNAUTHORIZED_MSG, null, 
                                                                    Locale.getDefault()));
        
        AUTH_FAILURE_RES = new ResponseEntity<>(authFailRES, HttpStatus.UNAUTHORIZED);
    }
    
    @Around("@annotation(com.gings.security.authentication.Authentication) || "
            + "within(@com.gings.security.authentication.Authentication *)")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        
        log.info("Starting to authenticate user info.");
        
        String jwt = httpServletRequest.getHeader(AUTHORIZATION);
        
        if (jwt == null) {
            log.info("JWT token from request header does not exist.");
            
            return AUTH_FAILURE_RES;
        }
        
        jwt = jwt.replace(BEARER_SCHEME, "");
        
        JWTService jwtService = jwtServiceManager.resolve(USING_TOKEN_INFO);
            
         try {

            UserAuthTokenInfo token = 
                        (UserAuthTokenInfo)jwtService.decode(new UserAuthTokenInfo(jwt));
            
            log.info("JWT Authentication succeeded for user : {}", token.getUid());
            
            GingsPrincipal principal = new GingsPrincipal(token.getUid(), token.getUserRole());
          
            Object[] args = Arrays.stream(pjp.getArgs())
                                  .map(arg -> {
                                     return (arg instanceof GingsPrincipal)? principal : arg;
                                  })
                                  .toArray();

            return pjp.proceed(args);

        } catch(TokenExpiredException e){
            log.info("Request user token expired.");
            
            return AUTH_FAILURE_RES;
        } catch(JWTVerificationException e) {
            String remote = getRequestAddr();
            
            log.error("Exception occurred while trying to authenticate user request.", e);
            log.warn("It might be illegal access!! Requesting remote user ip : {}", remote);
            
            return AUTH_FAILURE_RES;
        }
    }
    
    private String getRequestAddr() {
        String remote = httpServletRequest.getHeader(XFF_HEADER_NAME);
        
        if(remote == null) {
            remote = httpServletRequest.getRemoteAddr();
        }
        
        return remote;
    }
}

