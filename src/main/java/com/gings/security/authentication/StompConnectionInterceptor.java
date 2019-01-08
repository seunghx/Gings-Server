package com.gings.security.authentication;

import org.modelmapper.ModelMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gings.dao.UserMapper;
import com.gings.security.StompConnectingAuthentication;
import com.gings.security.WebSocketPrincipal;
import com.gings.security.jwt.JWTService;
import com.gings.security.jwt.JWTServiceManager;
import com.gings.security.jwt.UserAuthTokenInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StompConnectionInterceptor implements ChannelInterceptor {
    
    private final UserMapper userMapper;
    private final JWTServiceManager jwtServiceManager;
    
    private final ModelMapper modelMapper = new ModelMapper();
    
    public StompConnectionInterceptor(UserMapper userMapper, JWTServiceManager jwtServiceManager) {
        this.userMapper = userMapper;
        this.jwtServiceManager = jwtServiceManager;
    }
    
    /**
     * stage 관련 unsubscription 메세지가 전달될 경우 이를 오직 {@link SimpleBrokerMessageHandler}에만
     * 전달한다.<br>
     * 
     * 또한 메서드의 반환 타입을 고려하여 임의로 만든 길이 0의 메세지를 생성하여 특정 엔드포인트({@link MessageMapping)
     * 로 전달하게 하였다.이 엔드 포인트로의 메세지를 처리하는 핸들러 메서드는 전달 받은 메세지를 어디에도 전달하지 않기 때문에 
     * 결국 메세지는 무시된다.
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
                
        if(command == StompCommand.CONNECT) {
            String jwt = accessor.getFirstNativeHeader(JWTService.AUTHORIZATION);

            if(!isValidToken(jwt)) {
                log.warn("Authentication for connect request failed.");     
                
                throw new BadCredentialsException("Received invalid jwt");
            }
            
            jwt = jwt.replace(JWTService.BEARER_SCHEME,  "");
            
            log.info("Validated jwt token : {}", jwt);
            
            try {
                
                UserAuthTokenInfo tokenInfo = authenticateInternal(jwt);

                WebSocketPrincipal principal = 
                        modelMapper.map(userMapper.findByUserId(tokenInfo.getUid()), 
                                                                WebSocketPrincipal.class);
                
                // @AuthenticationPrincipal 가능할 경우 WebSocketPrincipal이 java.security.Principal 구현안하게.
                accessor.setUser(new StompConnectingAuthentication(principal));
                
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(new StompConnectingAuthentication(principal));
                SecurityContextHolder.setContext(context);
                
                return message;
                
            }catch(TokenExpiredException e){
                log.info("Request user token expired.");
                
                throw new CredentialsExpiredException("JWT token expired.", e);
            } catch(JWTVerificationException e) {
                
                log.warn("Invalid jwt token detected.");
                log.warn("It might be illegal access!! Requesting remote user ip : {}", accessor.getHost());
                
                throw new BadCredentialsException("Invalid JWT token.", e);
            }
        }
        
       return message;

    }
    

    private UserAuthTokenInfo authenticateInternal(String jwt) {
        
        UserAuthTokenInfo tokenInfo = new UserAuthTokenInfo(jwt);
        
        return (UserAuthTokenInfo)jwtServiceManager.resolve(tokenInfo.getClass())
                                                   .decode(tokenInfo);
    }
   /* 
    private Message<?> onConnectionAuthenticationFailed(Message<?> message){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        
        StompHeaderAccessor newHeaderAccessor = 
                                        StompHeaderAccessor.create(StompCommand.MESSAGE);
        
        newHeaderAccessor.setSessionId(accessor.getSessionId());
        newHeaderAccessor.setSessionAttributes(accessor.getSessionAttributes());
        newHeaderAccessor.setDestination(accessor.getDestination());
        newHeaderAccessor.setContentLength(0);
        
        return MessageBuilder.createMessage(new byte[0]
                                          , newHeaderAccessor.getMessageHeaders());
    }
    */
    
    private boolean isValidToken(String jwt) {

        if(StringUtils.isEmpty(jwt)) {
            log.warn("Received Empty jwt token.");
            
            return false;
        }
        
        if(!jwt.startsWith(JWTService.BEARER_SCHEME)) {
            log.warn("Invalid jwt token. token does not starts with Bearer");
            
            return false;
        }
        
        return true;
    }
    
}
