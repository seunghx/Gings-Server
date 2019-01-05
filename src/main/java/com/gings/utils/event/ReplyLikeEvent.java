package com.gings.utils.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ReplyLikeEvent extends ApplicationEvent{

   
    private static final long serialVersionUID = 1808848066502618129L;

    private final int replyId;
    private final String boardType;
    private final int userId;
    
    public ReplyLikeEvent(Object source,  int replyId, String boardType, int userId) {
        super(source);
        this.replyId = replyId;
        this.boardType = boardType;
        this.userId = userId;
    }
}
