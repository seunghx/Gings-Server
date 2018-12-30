package com.gings.controller;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.gings.security.Principal;
import com.gings.security.authentication.AuthAspect;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrincipalArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if(parameter == null) {
            log.info("Received null valued parameter.");
            
            throw new NullPointerException("Null value parameter detected.");
        }
        return Principal.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Principal resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                     NativeWebRequest webRequest, WebDataBinderFactory binderFactory) 
                                                                                    throws Exception {

        log.info("Starting to resolving argument.");
        
        try {
            Principal principal = 
                        (Principal)RequestContextHolder.getRequestAttributes()
                                                       .getAttribute(AuthAspect.PRINCIPAL, 
                                                                     RequestAttributes.SCOPE_REQUEST);
            if(principal == null) {
                log.error("Null value prinicpal detected. Checking authentication component required.");
                
                throw new IllegalStateException("Null valued principal detected.");
            }
            
            return principal;
        }catch(ClassCastException e) {
            log.info("Unknown principal object detected. Checking authentication component required.");
            
            throw new IllegalStateException("Unknown principal detected.", e);
        }
    }
}
