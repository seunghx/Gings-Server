package com.gings.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
public class AndroidPushNotificationsService {
    private static final String FIREBASE_SERVER_KEY = "AAAAH6QkkZk:APA91bE6hFysnwkFtZCx3beRpt87CJMSVVIFyRCBl2nNH0UdHuZHpgVT5CP6N_5Owf3PXOpoQ3H-nS30uAec3fXQ7Gt4vH-YjHRo6Fi-6-AGMVhRELM-iuoc9cKHQpk-u5LBbQHmr7o5";
    private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";

    @Async
    public CompletableFuture<String> send(HttpEntity<String> entity) {

        RestTemplate restTemplate = new RestTemplate();

        /**
         https://fcm.googleapis.com/fcm/send
         Content-Type:application/json
         Authorization:key=FIREBASE_SERVER_KEY*/

        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

        interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
        restTemplate.setInterceptors(interceptors);

        String firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL, entity, String.class);

        return CompletableFuture.completedFuture(firebaseResponse);
    }
}

