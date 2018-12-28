package com.gings.security;


import com.gings.utils.code.UserRole;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author seunghyun
 *
 */
@Getter
@Setter
public class UserAuthTokenInfo extends TokenInfo {
    
    private int uid;
    private UserRole userRole;
    
}
