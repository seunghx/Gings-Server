package com.gings.utils.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardBannedEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 5207308072424299645L;

    private final int userId;
    
    public BoardBannedEvent(Object source, int userId) {
        super(source);
        this.userId = userId;
    }
}
