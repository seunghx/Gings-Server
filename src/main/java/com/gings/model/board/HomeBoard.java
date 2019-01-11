package com.gings.model.board;

import com.gings.domain.board.BoardReply;
import com.gings.utils.code.BoardCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class HomeBoard {
    @Getter
    @Setter
    @ToString
    public static class HomeBoardAllRes implements Comparable<HomeBoardAllRes>{
        @NotNull
        private int boardId;
        @NotNull
        private int writerId;
        @NotBlank
        private String writer;

        private String writerImage;

        @NotBlank
        private String field;
        @NotBlank
        private String company;

        @NotBlank
        private String title;
        @NotBlank
        private String content;

        private int share;
        private LocalDateTime time;
        @NotNull
        private BoardCategory category;

        private List<String> images;
        private List<String> keywords;
        private int numOfReply;

        private int recommender;

        private boolean likeChk;

        @Override
        public int compareTo(HomeBoardAllRes o) {
            if (this.recommender < o.getRecommender()) {
                return 1;
            } else if (this.recommender > o.getRecommender()) {
                return -1;
            }
            return 0;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class HomeBoardOneRes {
        @NotNull
        private int boardId;
        @NotNull
        private int writerId;
        @NotBlank
        private String writer;
        private String writerImage;

        @NotBlank
        private String field;
        @NotBlank
        private String company;

        @NotBlank
        private String title;
        @NotBlank
        private String content;

        private int share;
        private LocalDateTime time;
        private BoardCategory category;

        private List<String> images;
        private List<String> keywords;
        private List<BoardReply> replys;

        private int numOfReply;
        private int recommender;

        private boolean likeChk;
    }
}
