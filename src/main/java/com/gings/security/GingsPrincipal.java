package com.gings.security;

import java.security.Principal;

import com.gings.utils.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class GingsPrincipal implements Principal{
    
    private Integer userId;
    private UserRole role;
    private String email;
    
    public GingsPrincipal() {
        
    }
    
    public GingsPrincipal(Integer userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
