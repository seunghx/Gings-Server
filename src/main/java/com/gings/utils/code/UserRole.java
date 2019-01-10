package com.gings.utils.code;

import java.util.Collection;
import java.util.function.Supplier;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

public enum UserRole {
    
    USER(() -> AuthorityUtils.createAuthorityList("ROLE_USER")), 
    ADMIN(() -> AuthorityUtils.createAuthorityList("ROLE_USER, ROLE_ADMIN"));
    
    private Supplier<Collection<GrantedAuthority>> authorityProvider;
    
    UserRole(Supplier<Collection<GrantedAuthority>> authorityProvider) {
        this.authorityProvider = authorityProvider;
    }
    
    public Collection<GrantedAuthority> getAuthorities(){
        return authorityProvider.get();
    }
}
