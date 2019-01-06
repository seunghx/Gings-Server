package com.gings.security.jwt;

import static com.auth0.jwt.JWT.require;

import org.springframework.beans.factory.annotation.Value;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailAuthWTService implements JWTService {
    
    private static final String AUTH_NUMBER_CLAIM_NAME = "authNumber";
    private static final String EMAIL_CLAIM_NAME = "email";
    
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.auth-number.issuer}")
    private String issuer;
    @Value("${jwt.auth-number.token-ttl.day}")
    private int expiredPeriod;
    
    @Override
    public String create(TokenInfo tokenInfo) {
        // null is not instanceof UserAuthTokenInfo
        if(!(tokenInfo instanceof EmailAuthTokenInfo)) {
            log.warn("Invalid Argument TokenInfo. This class does not support {}.", tokenInfo); 
            
            throw new IllegalStateException("Illegal tokenInfo detected.");
        }
        
        EmailAuthTokenInfo supportingTokenInfo = (EmailAuthTokenInfo)tokenInfo;
        
        try {
            return JWT.create()
                      .withIssuer(issuer)
                      .withClaim(AUTH_NUMBER_CLAIM_NAME, supportingTokenInfo.getAuthNumber())
                      .withClaim(EMAIL_CLAIM_NAME, supportingTokenInfo.getEmail())
                      .withExpiresAt(expiredAt(expiredPeriod))
                      .sign(algorithm(secret));
                      
        } catch (JWTCreationException jce) {
            log.info("Exception occurred while trying to create JWT token.");  
            
            throw jce;
        }
    }
    
    /**
     * @throws {@link JWTVerifier#verify(String)}
     */
    @Override
    public EmailAuthTokenInfo decode(TokenInfo tokenInfo) {
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
        
        return EmailAuthTokenInfo.class.isAssignableFrom(tokenInfo);
    
    }
    
    private DecodedJWT verifyToken(TokenInfo tokenInfo) {
       
        log.error("{}", tokenInfo);
        try {
            JWTVerifier jwtVerifier = require(algorithm(secret))
                                         .withIssuer(issuer)
                                         .withClaim(AUTH_NUMBER_CLAIM_NAME, 
                                                    ((EmailAuthTokenInfo)tokenInfo).getAuthNumber())
                                         .build();
          
            return jwtVerifier.verify(tokenInfo.getToken());
          
        }catch (JWTVerificationException jve) {
          
            log.info("Error occurred while trying to decode JWT token.");
          
            throw jve;
        }
    }
        
    private EmailAuthTokenInfo parseToken(DecodedJWT decodedJWT) {
        EmailAuthTokenInfo tokenInfo = new EmailAuthTokenInfo();
        
        tokenInfo.setEmail(decodedJWT.getClaim(EMAIL_CLAIM_NAME)
                                     .asString());
        
        return tokenInfo;
    }
}
