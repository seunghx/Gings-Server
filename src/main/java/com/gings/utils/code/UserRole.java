package com.gings.utils.code;


public enum UserRole implements Code {
    
    USER("USER"), ADMIN("ADMIN");
    
    private String code;
    
    UserRole(String code) {
        this.code = code;
    }
    
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean equalsByCode(String code) {
        return this.code.equals(code);
    }
    
    public static UserRole from(String code) {
        for(UserRole userRole : values()) {
            if(userRole.equalsByCode(code)){
                return userRole;
            }
        }
        
        throw new IllegalArgumentException("Invalid UserRole code.");
    }
}
