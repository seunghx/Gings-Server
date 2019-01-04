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
        private int id;
        private String content;
        private List<Integer> indexOfPrevImages;
        private List<MultipartFile> images;
    }

    @Getter
    @Setter
    public static class IntroduceRes {
        // private int introduceId;
        private String content;
        private List<MultipartFile> imgs;
    }

}
