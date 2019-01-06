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
public class Principal {
    public Integer userId;
    public UserRole role;
    public String email;
    
    public Principal() {
        
    }
    
    public Principal(Integer userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }
}
