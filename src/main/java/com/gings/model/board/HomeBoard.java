package com.gings.model.board;

import com.gings.domain.board.BoardReply;
import com.gings.utils.code.BoardCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class HomeBoard {
    @Getter
    @Setter
    public static class HomeBoardAllRes implements Comparable<HomeBoardAllRes>{
        private int boardId;

        private int writerId;
        private String writer;
        private String writerImage;

        private String field;
        private String company;

        private String title;
        private String content;
        private int share;
        private LocalDateTime time;
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
    public static class HomeBoardOneRes {
        private int boardId;

        private int writerId;
        private String writer;
        private String writerImage;

        private String field;
        private String company;

        private String title;
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
