package com.gings.model;

import com.gings.domain.Introduce;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class IntroduceModel {

    @Getter
    @Setter
    public static class IntroduceReq {
        //private int introduceId;
        private int id;    //사실상 introduceId야
        //아래부터 받는 애들
        private String content;
        private List<MultipartFile> images;

        private List<String> prevImagesUrl;
        private List<String> postImagesUrl;

    }

    @Getter
    @Setter
    public static class IntroduceRes {
        // private int introduceId;
        private String content;
        private List<MultipartFile> imgs;
    }

}
