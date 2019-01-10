package com.gings.security.jwt;


import com.gings.utils.code.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author seunghyun
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthTokenInfo extends TokenInfo {
    
    private int uid;
    private UserRole userRole;
    
    public UserAuthTokenInfo(String token) {
        super(token);
    }
    
}
