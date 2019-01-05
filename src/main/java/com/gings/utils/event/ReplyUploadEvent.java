package com.gings.utils.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
public class ReplyUploadEvent extends ApplicationEvent{

    
    private static final long serialVersionUID = 6035476108203114100L;
    
    
    private final int boardId;
    // 후에 enum으로 변경 예정
    private final String boardType;
    private final int userId;


    public ReplyUploadEvent(Object source, int boardId, String boardType, int userId) {
        super(source);
        this.boardId = boardId;
        this.boardType = boardType;
        this.userId = userId;
    }
        
}
