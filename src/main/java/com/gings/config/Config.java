package com.gings.config;

import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.thymeleaf.spring5.SpringTemplateEngine;

import nz.net.ultraq.thymeleaf.LayoutDialect;


/**
 * 
 * @author seunghyun
 *
 */
@EnableAsync
@Configuration
public class Config {
    
    @Autowired
    private SpringTemplateEngine templateEngine;
    
    @PostConstruct
    public void init() {
        templateEngine.addDialect(new LayoutDialect());
    }
    
    @Bean("taskExecutor")
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }
    
}
