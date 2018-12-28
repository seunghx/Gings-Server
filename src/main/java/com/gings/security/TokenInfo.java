package com.gings.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
public abstract class TokenInfo {
    private String token;    
}
