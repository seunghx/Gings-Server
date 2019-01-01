package com.gings.model;

import com.gings.domain.BoardKeyword;
import com.gings.domain.BoardReply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class UpBoard {
    @Getter
    @Setter
    @NotBlank
    public static class UpBoardReq {
        private int boardId;
        private int writerId;
        private String title;
        private String content;
        private String category;


        private MultipartFile[] images;
        private List<String> keywords;

        private int recommender;
        private int share;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UpBoardRes {
    }
}