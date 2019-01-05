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

    private final int userId;
    
    public GuestBoardUploadEvent(Object source, int userId) {
        super(source);
        this.userId = userId;
    }

}
