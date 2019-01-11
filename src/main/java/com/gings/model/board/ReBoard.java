package com.gings.model.board;

import com.gings.utils.ImageExtension;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class ReBoard {

    @Getter
    @Setter
    public static class ReBoardReq{
        private int replyId;


        private int boardId;

        private int writerId;
        private String writer;

        @NotBlank
        private String content;

        private LocalDateTime writeTime;

        private List<MultipartFile> images;

        private int recommender;

    }

    @Getter
    @Setter
    public static class ModifyReBoardReq{

        private int replyId;
        private int boardId;

        private int writerId;
        private String writer;

        @NotBlank
        private String content;
        private LocalDateTime writeTime;

        private List<String> prevImagesUrl;
        private List<@ImageExtension MultipartFile> postImages;

        private int recommender;
    }
}
