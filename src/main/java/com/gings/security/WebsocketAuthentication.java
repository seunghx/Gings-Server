package com.gings.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebsocketAuthentication implements Authentication {
    
    
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getCredentials() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getDetails() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getPrincipal() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        
    }

}
