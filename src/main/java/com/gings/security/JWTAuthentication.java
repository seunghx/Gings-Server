package com.gings.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class JWTAuthentication implements Authentication {

    private static final long serialVersionUID = 2700546458690739350L;
    
    private static final String PRINCIPAL_NAME_FORMAT = "Default user principal for user %s.";
    private static final String ANONYMOUS_USER_NAME = "unauthenticated anonymous user";
    
    private final UserAuthTokenInfo tokenInfo;
    
    public JWTAuthentication(UserAuthTokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }
    
    @Override
    public String getName() {
        
        String userId = null;
        
        if(tokenInfo == null || tokenInfo.getUid() == 0) {
            userId = ANONYMOUS_USER_NAME;
        }else {
            userId = String.valueOf(tokenInfo.getUid());
        }
        
        return String.format(PRINCIPAL_NAME_FORMAT, userId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       if(tokenInfo == null || tokenInfo.getUserRole() == null) {
           return null;
       }
        
        return tokenInfo.getUserRole().getAuthorities();
    }

    @Override
    public UserAuthTokenInfo getCredentials() {
        return tokenInfo;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Principal getPrincipal() {
        if(tokenInfo == null || tokenInfo.getUid() == 0 || tokenInfo.getUserRole() == null) {
            return null;
        }
        
        return new Principal(tokenInfo.getUid(), tokenInfo.getUserRole());
    }

    @Override
    public boolean isAuthenticated() {
        if(tokenInfo == null || tokenInfo.getUid() != 0 && tokenInfo.getUserRole() != null) {
            return true;
        }
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        return;
    }
}
