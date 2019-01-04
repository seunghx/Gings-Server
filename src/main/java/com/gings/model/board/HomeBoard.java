package com.gings.model.board;

import com.gings.domain.BoardReply;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class HomeBoard {
    @Getter
    @Setter
    public static class HomeBoardAllRes {
        private int boardId;
        private int writerId;

        private String writer;
        private String field;
        private String company;
        //private String writerImage;

        private String title;
        private String content;
        private int share;
        private LocalDateTime time;
        private String category;

        private List<String> images;
        private List<String> keywords;
        private int numOfReply;

        private int recommender;

    }

    @Getter
    @Setter
    public static class HomeBoardOneRes {
        private int boardId;
        private int writerId;

        private String writer;
        private String field;
        private String company;
        //private String writerImage;

        private String title;
        private String content;
        private int share;
        private LocalDateTime time;
        private String category;

        private List<String> images;
        private List<String> keywords;
        private List<BoardReply> replys;

        private int numOfReply;
        private int recommender;

    }
}
