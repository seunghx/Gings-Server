package com.gings.security;

/**
 * 
 * @author seunghyun
 *
 */
public interface JWTService {
    
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_SCHEME = "Bearer ";
    
    public void validate(TokenInfo tokenInfo);
    public TokenInfo decode(TokenInfo tokenInfo);
    public String create(TokenInfo tokenInfo);
    public boolean support(Class<? extends TokenInfo> tokenInfo);
}
