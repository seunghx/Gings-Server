package com.gings.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthNumberTokenInfo extends TokenInfo {

    private String authNumber;
    
    public AuthNumberTokenInfo(String token) {
        super(token);
    }
    
}
