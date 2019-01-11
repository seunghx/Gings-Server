package com.gings.service;

import com.gings.dao.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class FCMService {

    private final UserMapper userMapper;
    private final AndroidPushNotificationsService androidPushNotificationsService;

    public FCMService(UserMapper userMapper, AndroidPushNotificationsService androidPushNotificationsService) {
        this.userMapper = userMapper;
        this.androidPushNotificationsService = androidPushNotificationsService;
    }

    public String createFcm(final int receiverId, final String senderId,
                            String notfTitle, String notfBody ){
        JSONObject body = new JSONObject();
        String fcm = getFcm(receiverId);
        System.out.println("서버 토큰: "+fcm);
        body.put("to", fcm);

//        JSONObject notification = new JSONObject();
//        notification.put("title", notfTitle);
//        notification.put("body", notfBody);
//        notification.put("data", senderId);

        //body.put("notification", notification);

        JSONObject data = new JSONObject();
        data.put("title", notfTitle);
        data.put("body", notfBody);
        data.put("sender_id", senderId);
        body.put("data",data);
        System.out.println(body.toString());

        HttpEntity<String> request = new HttpEntity<>(body.toString());

        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();
        try {
            String firebaseResponse = pushNotification.get();
            log.info(body.toString());
            return firebaseResponse;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "";
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getFcm(final int id){
        return userMapper.getTokenOfFcm(id);
    }

}
