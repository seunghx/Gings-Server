package com.gings.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BoardReply {

    private int replyId;
    private int writerId;

    private String content;
    private LocalDateTime writeTime;

    private int recommender;


}
