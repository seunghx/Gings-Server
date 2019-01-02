package com.gings.security;

import java.time.Instant;
import java.time.Period;
import java.util.Date;

import com.auth0.jwt.algorithms.Algorithm;

/**
 * 
 * @author seunghyun
 *
 */
public interface JWTService {
    
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_SCHEME = "Bearer ";
    
    public TokenInfo decode(TokenInfo tokenInfo);
    public String create(TokenInfo tokenInfo);
    public boolean support(Class<? extends TokenInfo> tokenInfo);
    
    
    default Algorithm algorithm(String secret) {
        return Algorithm.HMAC256(secret);
    }
    
    default Date expiredAt(int expiredPeriod) {
        Instant ins = Instant.now()
                             .plus(Period.ofDays(expiredPeriod));
        
        return Date.from(ins);
    }
}
