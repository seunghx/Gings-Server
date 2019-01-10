package com.gings.security.jwt;


import org.springframework.beans.factory.annotation.Value;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
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

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.user-auth.issuer}")
    private String issuer;
    @Value("${jwt.user-auth.token-ttl.day}")
    private int expiredPeriod;

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
                      .withClaim(USER_ROLE_CLAIM_NAME, supportingTokenInfo.getUserRole().name())
                      .withExpiresAt(expiredAt(expiredPeriod))
                      .sign(algorithm(secret));
                      
        } catch (JWTCreationException jce) {
            log.info("Exception occurred while trying to create JWT token.");  
            
            throw jce;
        }
    }
    
    
    @Override
    public TokenInfo decode(TokenInfo tokenInfo) {

        if(tokenInfo == null) {
            log.info("Null value tokenInfo detected while trying to decode JWT token.");
            
            throw new NullPointerException("tokenInfo is null.");
        }
        
       DecodedJWT decoded = verifyToken(tokenInfo);
        
       return parseToken(decoded);
    }

    @Override
    public boolean support(Class<? extends TokenInfo> tokenInfo) {
        
        if(tokenInfo == null) {
            log.info("Null valued class parameter detected.");
            
            throw new NullPointerException("Class parameter tokenInfo is null.");
        }
        
        return UserAuthTokenInfo.class.isAssignableFrom(tokenInfo);
    }

    private DecodedJWT verifyToken(TokenInfo tokenInfo) {
        
        try {
            JWTVerifier jwtVerifier = require(algorithm(secret)).withIssuer(issuer)
                                                                .build();
            
            return jwtVerifier.verify(tokenInfo.getToken());
            
        }catch (JWTVerificationException jve) {
            
            log.info("Error occurred while trying to decode JWT token.");
            
            throw jve;
        }
    }
    
    private UserAuthTokenInfo parseToken(DecodedJWT decodedJWT) {
        UserAuthTokenInfo tokenInfo = new UserAuthTokenInfo();
        
        tokenInfo.setUid(decodedJWT.getClaim(USER_ID_CLAIM_NAME)
                                   .asInt());
        tokenInfo.setUserRole(UserRole.valueOf(decodedJWT.getClaim(USER_ROLE_CLAIM_NAME)
                                                         .asString()));
        
        return tokenInfo;
    }
}
