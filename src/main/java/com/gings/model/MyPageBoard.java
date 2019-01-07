package com.gings.model;

import com.gings.domain.BoardReply;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MyPageBoard {
    private int boardId;
    private int writerId;

    private String writer;

    private String title;
    private String content;
    private int share;
    private LocalDateTime time;
    private String category;

    private List<String> images;
    private List<String> keywords;
    private List<BoardReply> replys;

    private int recommender;
}
