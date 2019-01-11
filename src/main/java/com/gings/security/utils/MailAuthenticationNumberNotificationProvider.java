package com.gings.security.utils;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.extern.slf4j.Slf4j;
import nz.net.ultraq.thymeleaf.LayoutDialect;

/**
 * 
 * 인증 번호 전송의 역할을 수행하는 {@link AuthenticationNumberNotificationProvider} 구현으로 이
 * 클래스는 이메일을 통해 임시 비밀 번호를 전송한다.
 * 
 * 
 * @author leeseunghyun
 *
 */
@Slf4j
@Component
public class MailAuthenticationNumberNotificationProvider 
                                                implements AuthenticationNumberNotificationProvider {

    private static final String AUTH_NUMBER_KEY = "authNumber";
    private static final String IMG_CID = "identifier";
    private static final String MIME_ENCODING = "UTF-8";
    private static final String SUBJECT = "깅스 계정 확인 인증번호를 보내드립니다.";
    
    @Value("${thymeleaf.template.authNumber.location}")
    private String templateLocation;
    @Value("${thymeleaf.template.authNumber.representing-image.location}")
    private String representingImgLocation;
    @Value("${spring.mail.default-sender-id}")
    private String senderId;
    
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;
   
    public MailAuthenticationNumberNotificationProvider(SpringTemplateEngine templateEngine, 
                                                        JavaMailSender mailSender, 
                                                        ResourceLoader resourceLoader) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.resourceLoader = resourceLoader;
    }
    
    @Override
    public void sendAuthenticationNumber(String email, String authNumber) {
        
        validate(email, authNumber);

        String htmlMessage = getMailMessage(authNumber);
         
        try {
            MimeMessage message = mailSender.createMimeMessage();
            
            MimeMessageHelper helper = new MimeMessageHelper(message, true, MIME_ENCODING);
            helper.setFrom(senderId);
            helper.setTo(email);
            helper.setText(htmlMessage, true);
            helper.setSubject(SUBJECT);
            helper.addInline(IMG_CID, resourceLoader.getResource(representingImgLocation));
            
            mailSender.send(message);
        } catch (MessagingException e) {
            if(log.isInfoEnabled()){
                log.info("Sending mail with authentication number to {} failed.", email);
                log.info("Converting checked exception {} to unchecked exception.", e.toString());
            }
            
            throw new AuthNumberNotificationFailedException("Sending mail faild.", e);
        }
    }
    
    private void validate(String email, String authNumber) {
        
        if (StringUtils.isEmpty(email)) {
            log.warn("Argument email is empty. Checking code required.");
            throw new IllegalArgumentException("Argument destination is empty.");
        }
        if (StringUtils.isEmpty(authNumber)) {
            log.warn("Argument temporaryPassword is empty. Checking code required.");
            throw new IllegalArgumentException("Argument temporaryPassword is empty.");
        }
    }

    private String getMailMessage(String authNumber) {

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(AUTH_NUMBER_KEY, authNumber);

        Context context = new Context();
        context.setVariables(parameterMap);

        return templateEngine.process(templateLocation, context);
    }
}
