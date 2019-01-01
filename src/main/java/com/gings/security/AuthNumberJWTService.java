package com.gings.security;

import static com.auth0.jwt.JWT.require;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gings.utils.code.UserRole;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthNumberJWTService implements JWTService {
    
    private static final String AUTH_NUMBER_CLAIM_NAME = "authNumber";

    @Value("${jwt.user-auth.secret}")
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
        DecodedJWT decoded = validateInternal(tokenInfo);
        
        return null;
    }
   
    @Override
    public String create(TokenInfo tokenInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean support(Class<? extends TokenInfo> tokenInfo) {
        if(tokenInfo == null) {
            log.info("Null valued class parameter detected.");
            
            throw new NullPointerException("Class parameter tokenInfo is null.");
        }
        
        return UserAuthTokenInfo.class.isAssignableFrom(tokenInfo);
    
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
