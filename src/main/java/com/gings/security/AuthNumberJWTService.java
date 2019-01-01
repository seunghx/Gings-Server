package com.gings.security;

import static com.auth0.jwt.JWT.require;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthNumberJWTService implements JWTService {
    
    private static final String AUTH_NUMBER_CLAIM_NAME = "authNumber";

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.auth-number.issuer}")
    private String issuer;
    @Value("${jwt.auth-number.token-ttl.day}")
    private int expiredPeriod;
    
    private Algorithm algorithm;
    
    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(secret);
    }
    
    /**
     * @throws {@link JWTVerifier#verify(String)}
     */
    @Override
    public AuthNumberTokenInfo decode(TokenInfo tokenInfo) {
        validateInternal(tokenInfo);
        
        return null;
    }
   
    @Override
    public String create(TokenInfo tokenInfo) {
        // null is not instanceof UserAuthTokenInfo
        if(!(tokenInfo instanceof AuthNumberTokenInfo)) {
            log.warn("Invalid Argument TokenInfo. This class does not support {}.", tokenInfo); 
            
            throw new IllegalStateException("Illegal tokenInfo detected.");
        }
        
        AuthNumberTokenInfo supportingTokenInfo = (AuthNumberTokenInfo)tokenInfo;
        
        try {
            return JWT.create()
                      .withIssuer(issuer)
                      .withClaim(AUTH_NUMBER_CLAIM_NAME, supportingTokenInfo.getAuthNumber())
                      .withExpiresAt(expiredAt(expiredPeriod))
                      .sign(algorithm);
                      
        } catch (JWTCreationException jce) {
            log.info("Exception occurred while trying to create JWT token.");  
            
            throw jce;
        }
    }
    
    @Override
    public boolean support(Class<? extends TokenInfo> tokenInfo) {
        if(tokenInfo == null) {
            log.info("Null valued class parameter detected.");
            
            throw new NullPointerException("Class parameter tokenInfo is null.");
        }
        
        return AuthNumberTokenInfo.class.isAssignableFrom(tokenInfo);
    
    }

    @Override
    public void validate(TokenInfo tokenInfo) {
        validateInternal(tokenInfo);
    }
    
    
    private DecodedJWT validateInternal(TokenInfo tokenInfo) {
        if(tokenInfo == null) {
            log.info("Null value tokenInfo detected while trying to decode JWT token.");
          
            throw new NullPointerException("tokenInfo is null.");
        }
      
        try {
            JWTVerifier jwtVerifier = require(algorithm)
                                         .withIssuer(issuer)
                                         .withClaim(AUTH_NUMBER_CLAIM_NAME, 
                                                    ((AuthNumberTokenInfo)tokenInfo).getAuthNumber())
                                         .build();
          
            return jwtVerifier.verify(tokenInfo.getToken());
          
        }catch (JWTVerificationException jve) {
          
            log.info("Error occurred while trying to decode JWT token.");
          
            throw jve;
        }
    }
}
