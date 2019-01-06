package com.gings.security.jwt;

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
    
    /**
     * 
     * @param  tokenInfo  단지 문자열 jwt token만으로 verify 및 decode가 가능한 경우도 있지만  
     *                    인증 번호나 IP 주소와 같은 값을 포함한 verification이 필요할 경우도 있어 
     *                    파라미터 타입을 문자열이 아닌 객체 타입으로 지정함.
     */
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
