package com.gings.security.jwt;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * base container for JWT Token config info
 * 
 * @author seunghyun
 *
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class TokenInfo {
    private String token;    
}
