package com.gings.service;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.gings.dao.UserMapper;
import com.gings.domain.User;
import com.gings.domain.PushNotification;
import com.gings.utils.NotificationEventListener;
import com.gings.utils.event.BoardBannedEvent;
import com.gings.utils.event.BoardLikeEvent;
import com.gings.utils.event.GuestBoardUploadEvent;
import com.gings.utils.event.ReplyLikeEvent;
import com.gings.utils.event.ReplyUploadEvent;


import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * 
 * {@link PushNotification} 관련 로직을 수행하는 이 서비스 클래스에서 
 * {@link NotificationEventListener} 클래스를 구현하는 이유는 푸시 알림시 푸시 알림에 대한
 * 정보를 저장해야 하기 때문이다. 이런 점으로 볼때 위에서 {@link PushNotificationService}의 
 * 클래스 주석에서 말한 "다른 관점"이 더 적절해 보이기도 한다.
 * 
 * {@code userMapper}를 보면 {@link com.gings.dao.UserMapper}에 대한 참조임을 알 수 있는데
 * 이럴 거면 {@link UserService}에서 push notification을 수행하게 하는 것이 더 나아보일 수도 있으나 
 * 매번 push notification이 수행될 때마다 {@link PushNotificationService}이 저장되어야 하기 때문에 
 * {@link PushNotificationService} 관련 처리 로직을 수행하는 이 클래스에서 push notification을 하게 하였다.
 * (다른 말로 {@link NotificationEventListener}를 구현하게 하였다.)
 * 
 * 
 * @see PushNotificationService
 * 
 * @author seunghyun
 *
 */
@Slf4j
@Service
public class PushNotificationServiceImpl implements NotificationEventListener {

    private static final String PUSH_DESTINATION = "/queue/notification";
    
    private static final String BOARD_BANNED = "response.event.onBoardBanned";
    private static final String BOARD_LIKE = "response.event.board-like";
    private static final String ANSWER_REPLY_LIKE = "response.event.reply-like.answer";
    private static final String COWORKING_REPLY_LIKE = "response.event.reply-like.coworking";
    private static final String INSPIRATION_REPLY_LIKE = "response.event.reply-like.inspiration";
    private static final String ANSWER_REPLY_UPLOAD = "response.event.reply-upload.answer";
    private static final String INSPIRATION_REPLY_UPLOAD = "response.event.reply-upload.inspiration";
    private static final String COWORKING_REPLY_UPLOAD = "response.event.reply-upload.coworking";
    private static final String GUEST_BOARD_UPLOAD = "response.event.guest-board-upload";
               
    private final MessageSource messageSource;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserMapper userMapper;
    
    public PushNotificationServiceImpl(MessageSource messageSource, 
                                       SimpMessagingTemplate messagingTemplate,
                                       UserMapper userMapper) {
        
        this.messageSource = messageSource;
        this.messagingTemplate = messagingTemplate;
        this.userMapper = userMapper;
    }
    
    /**
     * 
     * 아래 코드를 보면 {@link Locale#getDefault()}를 호출하는데, 더 고민해보고 {@link HttpServletRequest}
     * 사용 고민하기. 
     * 
     * -> getDefault 쓸거면 messageSource 사용이유가 떨어지고 반대로 HttpServletRequest를 쓰면
     * 비동기로 동작하게 할 건데 제대로 가져올 수 있는건지 알 수 없음 그렇다고 event 인자로 받자니 service 레이어에
     * 특정 웹 기술 종속이 너무 심해짐(이벤트 보내는쪽에도 HttpServletRequest사용해야하니까 - Locale 받는 것도 
     * 마찬가지)
     * 
     */
    @Override
    public void onBoardBannedEvent(BoardBannedEvent event) {
        int userId = event.getWriterId();
        
        User user = userMapper.findByUserId(userId);    
        
        String message = messageSource.getMessage(BOARD_BANNED, null, Locale.getDefault());
        
       // messagingTemplate.convertAndSendToUser(user.getEmail(), PUSH_DESTINATION, payload);
    }

    @Override
    public void onBoardLikeEvent(BoardLikeEvent event) {
        String message = messageSource.getMessage(BOARD_LIKE, null, Locale.getDefault());
        
    }

    @Override
    public void onGuestBoardUploadEvent(GuestBoardUploadEvent event) {
        
        String message = messageSource.getMessage(GUEST_BOARD_UPLOAD, null, Locale.getDefault());

        
    }
    
    // 내일 board type enum으로 변경
    // enum이 각각 자신에 맞는 메세지 이름 반환하게하는 방법 -> 코드는 깔끔해지나 보드 타입 이늄이 메세지 이름을 나타내기 위해 
    // 쓰이는 클래스는 아님 
    @Override
    public void onReplyLikeEvent(ReplyLikeEvent event) {
        //
        String boardType = null;
        
        String message = null;
        
        if("QUESTION".equals(boardType)) {
            message = messageSource.getMessage(ANSWER_REPLY_LIKE, null, Locale.getDefault());
        }else if("INSPIRATION".equals(boardType)) {
            message = messageSource.getMessage(INSPIRATION_REPLY_LIKE, null, Locale.getDefault());
        }else if("COWORKING".equals(boardType)) {
            message = messageSource.getMessage(COWORKING_REPLY_LIKE, null, Locale.getDefault());
        }else {
            log.warn("Invalid board type detected. Invalid board type : {}", boardType);
            throw new IllegalStateException("Invalid board type detected.");
        }
        
        /*
         * 내일 아래로 변경
         * if(boardType == BoardyType.QUESTION){
         * }else if(boardType == BoardType.INSPIRATION){
         * }else if(boardType == BoardType.COWORKING){
         * }
         */
       // PushNotification pushNotification = new PushNotification(event.get);
        
    }
    
    
    // 내일 board type enum으로 변경
    @Override
    public void onReplyUploadEvent(ReplyUploadEvent event) {
        String message = messageSource.getMessage(BOARD_LIKE, null, Locale.getDefault());
        
    }

}

/*
 * 채팅 -> connection 시 유저 정보 가져와 SecurityContextHolder에 저장. 
 * 
 * 매번 메세지마다 유저 id또는 email로부터 user name  가져오지 말고 AuthenticationPrincipal.getPrincipal사용.
 * 
 * 헤더에서 name 가져오는 것은 완벽하게 안전한 방법이라고할수는없다.
 * 
 */
