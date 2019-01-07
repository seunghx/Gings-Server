package com.gings.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gings.domain.UserKeyword;
import com.gings.model.board.UpBoard;

import com.gings.utils.code.Region;
import com.gings.utils.code.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class MyPage {
    private int id;
    private String name;
    private Region region;
    private String job;
    private String company;
    private String field;
    private Status status;
    private String image;   //프로필
    private boolean coworkingEnabled;
    private List<String> keywords;

    @JsonIgnore
    private MultipartFile imgFile;

    @Setter
    @Getter
    public static class MyPageOther{
        private int id;
        private String name;
        //private String image;    //프로필
        private String field;
        //private String status;
        private boolean coworkingEnabled;
    }

    @Setter
    @Getter
    public static class MyPageIntro{
        //private MyPageOther myPageOther;
        private String content;
        private List<MultipartFile> imgs;
        private LocalDateTime time;
    }

    @Getter
    @Setter
    public static class MyPageActive{
        private UpBoard upBoard;
    }

    @Getter
    @Setter
    public static class MyPageProfile{
        private String image;
    }

    @Getter
    @Setter
    public static class MyPagePwdRes{
        private static final String INVALID_PWD = "비밀 번호는 7~14 글자의 영문 대소문자 및 숫자로 구성되어야 합니다.";

        @Pattern(regexp = "^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{7,14}$", message = INVALID_PWD)
        private String pwd;

        private String oldPwd;
    }
}
