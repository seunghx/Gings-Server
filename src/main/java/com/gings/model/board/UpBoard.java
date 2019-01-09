package com.gings.model.board;

import com.gings.domain.BoardKeyword;
import com.gings.domain.BoardReply;
import com.gings.utils.code.BoardCategory;
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
    public static class UpBoardReq {
        private int boardId;
        private int writerId;

        private String title;
        private String content;
        private BoardCategory category;


        private List<MultipartFile> images;
        private List<String> keywords;

        private int recommender;
        private int share;

        private boolean likeChk;

    }

    @Getter
    @Setter
    public static class UpBoardAllRes {
        private int boardId;
        private int writerId;

        private String writer;

        private String title;
        private String content;
        private int share;
        private LocalDateTime time;
        private BoardCategory category;

        private List<String> images;
        private List<String> keywords;

        private int reply;

        private int recommender;

    }

    @Getter
    @Setter
    public static class UpBoardOneRes {
        private int boardId;
        private int writerId;

        private String writer;

        private String title;
        private String content;
        private int share;
        private LocalDateTime time;
        private BoardCategory category;

        private List<String> images;
        private List<String> keywords;

        private List<BoardReply> replys;

        private int recommender;

        private boolean likeChk;

    }
}
