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

    public String createFcm(final int userId, final String title, final String body){
        JSONObject jsonBody = new JSONObject();
        String fcm = this.getFcm(userId);
        jsonBody.put("to", fcm);

        JSONObject notification = new JSONObject();
        notification.put("title", title); // title
        notification.put("body", body); // body

        jsonBody.put("notification", notification);

        HttpEntity<String> request = new HttpEntity<>(body);

        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();
        try {
            String firebaseResponse = pushNotification.get();
            return firebaseResponse;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (
                ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFcm(final int id){
        return userMapper.getTokenOfFcm(id);
    }

}
