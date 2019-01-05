package com.gings.utils;

import org.springframework.context.event.EventListener;

import com.gings.utils.event.BoardBannedEvent;
import com.gings.utils.event.BoardLikeEvent;
import com.gings.utils.event.GuestBoardUploadEvent;
import com.gings.utils.event.ReplyLikeEvent;
import com.gings.utils.event.ReplyUploadEvent;

public interface NotificationEventListener {
    
    @EventListener
    public void onBoardBannedEvent(BoardBannedEvent event);
    
    @EventListener
    public void onBoardLikeEvent(BoardLikeEvent event);
    
    @EventListener
    public void onGuestBoardUploadEvent(GuestBoardUploadEvent event);
    
    @EventListener
    public void onReplyLikeEvent(ReplyLikeEvent event);
    
    @EventListener
    public void onReplyUploadEvent(ReplyUploadEvent event);
    
}
