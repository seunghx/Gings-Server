package com.gings.model;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class Login {
    
    private static final String MSG_EMAIL_BLANK = "이메일을 입력하세요.";
    private static final String MSG_PASSWORD_BLANK = "패스워드를 입력하세요.";
    
    @Getter
    @Setter
    @AllArgsConstructor
    public static class LoginReq{
        
        @NotBlank(message = MSG_EMAIL_BLANK)
        private String email;
        @NotBlank(message = MSG_PASSWORD_BLANK)
        private String pwd;
        
        private boolean loginKeeped;
    }
    
    @Getter
    @Setter
    @AllArgsConstructor
    public static class LoginRes {
        
        private boolean firstLogin;
    }
}
