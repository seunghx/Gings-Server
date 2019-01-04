package com.gings.model;

import com.gings.domain.UserKeyword;
import com.gings.model.board.UpBoard;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class MyPage {
    private int id;
    private String name;
    private String region;
    private String job;
    private String company;
    private String field;
    private String status;
    private String image;   //프로필
    private boolean coworkingEnabled;
    private List<UserKeyword> keywords;

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

//    @Getter
//    @Setter
//    public static class MyPageIntroReq{
//        private int introduceId;
//        private int id;
//        private String content;
//        private List<String> imgs = new ArrayList<>();
//    }


}
