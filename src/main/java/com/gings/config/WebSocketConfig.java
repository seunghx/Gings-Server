package com.gings.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


/**
 * 
 * @author seunghyun
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    private static final String WS_CONNECT = "/connect";
    
    /*
     * 우선 rabbitmq 사용 안함.
     * 
     * @Value("${spring.rabbitmq.host}")
     * private String rabbitHost;
     * @Value("${spring.rabbitmq.port}")
     * private int rabbitPort;
     * @Value("${spring.rabbitmq.username}")
     * private String rabbitUser;
     * @Value("${spring.rabbitmq.password}")
     * private String rabbitPass;
     */
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config.enableSimpleBroker("/topic", "/queue");
       // config.enableStompBrokerRelay("/topic", "queue").setRelayHost(rabbitHost)
       //                                        .setRelayPort(rabbitPort)
       //                                        .setClientLogin(rabbitUser)
       //                                        .setClientPasscode(rabbitPass);
        config.setApplicationDestinationPrefixes("/");

    }

    // 후에 웹 채팅 추가할 경우 registrty.withSockJS() 추가 예정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WS_CONNECT)
                .setAllowedOrigins("*");
               
    }

}
