package com.gings.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class BoardReply {

    private int replyId;

    private int writerId;
    private String writer;
    private String writerImage;

    private String content;
    private LocalDateTime writeTime;

    private List<String> images;

    private int recommender;

    private boolean likeChk;

}
