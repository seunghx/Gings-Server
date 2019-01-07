package com.gings.security;

import com.gings.utils.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class GingsPrincipal {
    
    private Integer userId;
    private UserRole role;
    private String email;
    
    public GingsPrincipal() {
        
    }
    
    public GingsPrincipal(Integer userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }
}
