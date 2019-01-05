package com.gings.utils.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardLikeEvent extends ApplicationEvent {

    
    private static final long serialVersionUID = 7676214081469187223L;
    
    private final int boardId;
    private final String boardType;
    private final int userId;
    
    public BoardLikeEvent(Object source,  int boardId, String boardType, int userId) {
        super(source);
        this.boardId = boardId;
        this.boardType = boardType;
        this.userId = userId;
    }
}
