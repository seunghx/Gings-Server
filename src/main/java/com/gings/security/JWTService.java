package com.gings.security;

/**
 * 
 * @author seunghyun
 *
 */
public interface JWTService {
    public TokenInfo decode(TokenInfo tokenInfo);
    public String create(TokenInfo tokenInfo);
    public boolean support(Class<? extends TokenInfo> tokenInfo);
}
