package com.gings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class GingsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GingsApplication.class, args);
    }
}

