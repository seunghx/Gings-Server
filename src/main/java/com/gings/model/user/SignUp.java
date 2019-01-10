package com.gings.model.user;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.gings.utils.code.UserRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUp {
    
    private static final String EMPTY_NAME = "사용자 이름을 입력하세요.";
    //private static final String EMPTY_EMAIL = "사용자 이메일을 입력하세요.";
    private static final String EMPTY_PWD = "비밀번호를 입력하세요.";
    private static final String EMPTY_AUTH_NUMBER = "인증번호를 입력하세요.";
    private static final String INVALID_PWD = "비밀 번호는 7~14 글자의 대소문자를 구분한 영문자와 숫자 및 특수기호로 구성되어야 합니다.";
    private static final String INVALID_AUTH_NUMBER = "인증 번호가 올바르지 않습니다.";
    
    @NotBlank(message = EMPTY_NAME)
    private String name;
    // @NotBlank(message = EMPTY_EMAIL)
    // 일단 email은 전달 안하기로.
    private String email;
    @NotBlank(message = EMPTY_PWD)
    @Pattern(regexp = "^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{7,14}$", message = INVALID_PWD)
    private String pwd;
    // 아직 일반 유저 밖에 없으니 정의만해놓음.
    private UserRole role = UserRole.USER;

    @NotBlank(message = EMPTY_AUTH_NUMBER)

    @Pattern(regexp="^[0-9]{4}$", message = INVALID_AUTH_NUMBER)
    private String authNumber;
    
    
    public static class EmailReq {
        
        private static final String NOT_BLANK_MSG = "이메일을 입력하세요.";
        
        @Getter
        @Setter
        @NotBlank(message = NOT_BLANK_MSG)
        private String email;
    }
    
}
