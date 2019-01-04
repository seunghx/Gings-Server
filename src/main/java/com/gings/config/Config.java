package com.gings.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;

import nz.net.ultraq.thymeleaf.LayoutDialect;


/**
 * 
 * @author seunghyun
 *
 */
@Configuration
public class Config {
    
    @Autowired
    private SpringTemplateEngine templateEngine;
    
    @PostConstruct
    public void init() {
        templateEngine.addDialect(new LayoutDialect());
    }
    
}
