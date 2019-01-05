package com.gings.utils.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 
 * 
 * 
 * 
 * @author seunghyun
 *
 */
@Getter
@Setter
@ToString
public class ReplyUploadEvent extends ApplicationEvent{

    private static final long serialVersionUID = 6035476108203114100L;
    
    private final int boardId;
    private final int uploaderId;

    /**
     * 
     * @param boardId - 답글 상세보기 페이지가 존재하지 않기 때문에 푸시 알림을 클릭할 경우 답글이 올라간 해당 보드로 
     *                  <br>이동해야 한다. 그러므로 replyId가 아닌 boardId만 생성자 파라미터로 받으면 된다.
     * 
     */
    public ReplyUploadEvent(Object source, int boardId, int uploaderId) {
        super(source);
        this.boardId = boardId;
        this.uploaderId = uploaderId;
    }
        
}
