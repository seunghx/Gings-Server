package com.gings.domain.board;

import java.time.LocalDateTime;
import java.util.List;

import com.gings.utils.code.BoardCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Board {
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




    @Getter
    @Setter
    @ToString
    public static class BoardRecommend {
        private List<Integer> recommendBoardIdList;
    }

    @Getter
    @Setter
    @ToString
    public static class BoardBlock {
        private List<Integer> blockBoardIdList;
    }

    @Getter
    @Setter
    @ToString
    public static class Black {
        private List<Integer> blackList;
    }
}
