package com.gings.model.user;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUp {
    
    private static final String EMPTY_NAME = "사용자 이름을 입력하세요.";
    private static final String EMPTY_EMAIL = "사용자 이메일을 입력하세요.";
    private static final String EMPTY_PWD = "비밀번호를 입력하세요.";
    private static final String INVALID_PWD = "비밀 번호는 7~14 글자의 영문 대소문자 및 숫자로 구성되어야 합니다.";
    private static final String INVALID_AUTH_NUMBER = "인증 번호가 올바르지 않습니다.";
    
    
    @NotBlank(message = EMPTY_NAME)
    private String name;
    @NotBlank(message = EMPTY_EMAIL)
    private String email;
    @NotBlank(message = EMPTY_PWD)
    @Pattern(regexp = "^[a-zA-Z0-9]{7,14}$", message = INVALID_PWD)
    private String pwd;
    @Pattern(regexp="^[0-9]{4}$", message = INVALID_AUTH_NUMBER)
    private String authNumber;
    
    
    public static class EmailReq {
        
        private static final String NOT_BLANK_MSG = "이메일을 입력하세요.";
        
        @Getter
        @Setter
        //@Email
        @NotBlank(message = NOT_BLANK_MSG)
        private String email;
    }
    
}