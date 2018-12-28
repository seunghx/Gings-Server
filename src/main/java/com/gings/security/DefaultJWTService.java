package com.gings.security;

import java.time.Instant;
import java.time.Period;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gings.utils.code.UserRole;

import static com.auth0.jwt.JWT.require;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 주석 나중에 하기
 * 
 * @author seunghyun
 *
 */
@Slf4j
public class DefaultJWTService implements JWTService {
    
    private static final String USER_ID_CLAIM_NAME = "uid";
    private static final String USER_ROLE_CLAIM_NAME = "role";
    

    @Value("${jwt.user-auth.secret}")
    private String secret;
    @Value("${jwt.user-auth.issuer}")
    private String issuer;
    @Value("${jwt.user-auth.token-ttl.day}")
    private int expiredPeriod;
    
    private Algorithm algorithm;
    
    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(secret);
    }
    
    @Override
    public TokenInfo decode(TokenInfo tokenInfo) {
        
        if(tokenInfo == null) {
            log.info("Null value tokenInfo detected while trying to decode JWT token.");
            
            throw new NullPointerException("tokenInfo is null.");
        }
        
        try {
            JWTVerifier jwtVerifier = require(algorithm).withIssuer(issuer)
                                                        .build();
            
            DecodedJWT decodedJWT = jwtVerifier.verify(tokenInfo.getToken());
            
            return parseToken(decodedJWT);
        }catch (JWTVerificationException jve) {
            
            log.info("Error occurred while trying to decode JWT token.");
            
            throw jve;
        }
    }
    
    private UserAuthTokenInfo parseToken(DecodedJWT decodedJWT) {
        UserAuthTokenInfo tokenInfo = new UserAuthTokenInfo();
        tokenInfo.setUid(decodedJWT.getClaim(USER_ID_CLAIM_NAME).asInt());
        tokenInfo.setUserRole(UserRole.from(decodedJWT.getClaim(USER_ROLE_CLAIM_NAME).asString()));
        
        return tokenInfo;
    }

    @Override
    public String create(TokenInfo tokenInfo) {
        
        // null is not instanceof UserAuthTokenInfo
        
        if(!(tokenInfo instanceof UserAuthTokenInfo)) {
            log.warn("Invalid Argument TokenInfo. This class does not support {}.", tokenInfo); 
            
            throw new IllegalStateException("Illegal tokenInfo detected.");
        }
        
        UserAuthTokenInfo supportingTokenInfo = (UserAuthTokenInfo)tokenInfo;
        
        try {
            return JWT.create()
                      .withIssuer(issuer)
                      .withClaim(USER_ID_CLAIM_NAME, supportingTokenInfo.getUid())
                      .withClaim(USER_ROLE_CLAIM_NAME, supportingTokenInfo.getUserRole().getCode())
                      .withExpiresAt(expiredAt())
                      .sign(algorithm);
                      
        } catch (JWTCreationException jce) {
            log.info("Exception occurred while trying to create JWT token.");  
            
            throw jce;
        }
    }
    
    private Date expiredAt() {
        return Date.from(Instant.now()
                                .plus(Period.ofDays(expiredPeriod)));
    }


    @Override
    public boolean support(Class<? extends TokenInfo> tokenInfo) {
        
        if(tokenInfo == null) {
            log.info("Null valued class parameter detected.");
            
            throw new NullPointerException("Class parameter tokenInfo is null.");
        }
        
        return UserAuthTokenInfo.class.isAssignableFrom(tokenInfo);
    }

}
