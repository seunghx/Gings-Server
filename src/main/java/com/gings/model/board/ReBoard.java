package com.gings.model.board;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class ReBoard {

    @Getter
    @Setter
    public static class ReBoardReq{
        private int replyId;
        private int boardId;
        private int writerId;

        private String content;
        private LocalDateTime writeTime;

        private List<MultipartFile> images;

        private int recommender;
    }

    @Getter
    @Setter
    public static class ReBoardRes{
        private String name;
        private String content;
        private LocalDateTime writeTime;
    }
}
