package com.gings.utils;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.gings.utils.event.BoardBannedEvent;
import com.gings.utils.event.BoardLikeEvent;
import com.gings.utils.event.GuestBoardUploadEvent;
import com.gings.utils.event.ReplyLikeEvent;
import com.gings.utils.event.ReplyUploadEvent;

public interface NotificationEventListener {
    
    @Async("threadPoolTaskExecutor")
    @EventListener
    public void onBoardBannedEvent(BoardBannedEvent event);
    
    @Async("threadPoolTaskExecutor")
    @EventListener
    public void onBoardLikeEvent(BoardLikeEvent event);
    
    @Async("threadPoolTaskExecutor")
    @EventListener
    public void onGuestBoardUploadEvent(GuestBoardUploadEvent event);
    
    @Async("threadPoolTaskExecutor")
    @EventListener
    public void onReplyLikeEvent(ReplyLikeEvent event);
    
    @Async("threadPoolTaskExecutor")
    @EventListener
    public void onReplyUploadEvent(ReplyUploadEvent event);
    
}
