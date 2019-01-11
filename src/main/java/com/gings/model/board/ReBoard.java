package com.gings.model.board;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class ReBoard {

    @Getter
    @Setter
    public static class ReBoardReq{
        private int replyId;

        @NotBlank
        private int boardId;

        private int writerId;
        private String writer;

        @NotBlank
        private String content;

        private LocalDateTime writeTime;

        @NotBlank
        private List<MultipartFile> images;

        private int recommender;

    }

    @Getter
    @Setter
    public static class ModifyReBoardReq{
        @NotBlank
        private int replyId;
        @NotBlank
        private int boardId;

        private int writerId;
        private String writer;

        @NotBlank
        private String content;
        private LocalDateTime writeTime;

        private List<String> prevImagesUrl;
        private List<MultipartFile> postImages;

        private int recommender;
    }
}
