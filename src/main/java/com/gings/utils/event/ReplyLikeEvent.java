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
    private final int likerId;
    
    public ReplyLikeEvent(Object source, int replyId, int likerId) {
        super(source);
        
        this.replyId = replyId;
        this.likerId = likerId;
    }
}
