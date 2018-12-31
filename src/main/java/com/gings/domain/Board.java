package com.gings.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Board {
    private int boardId;
    private int writerId;
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
