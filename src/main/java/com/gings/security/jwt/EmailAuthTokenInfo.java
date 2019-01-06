package com.gings.security.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailAuthTokenInfo extends TokenInfo {

    private String authNumber;
    private String email;
    
    public EmailAuthTokenInfo(String token, String authNumber) {
        super(token);
        this.authNumber = authNumber;
    }
}
