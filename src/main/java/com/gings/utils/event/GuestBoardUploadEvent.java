package com.gings.utils.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GuestBoardUploadEvent extends ApplicationEvent{
     
    private static final long serialVersionUID = 4089870190152778313L;

    private final int ownerId;
    private final int guestId;
    
    /**
     * 
     * @param ownerId - 프로필 주인 id
     * @param guestId - 게스트 보드 작성자 id
     * 
     */
    public GuestBoardUploadEvent(Object source, int ownerId, int guestId) {
        super(source);
        this.guestId = guestId;
        this.ownerId = ownerId;
    }

}
