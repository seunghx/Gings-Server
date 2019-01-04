package com.gings.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gings.utils.code.UserRole;

import jdk.internal.jline.internal.Log;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TempController {
    
    @Getter
    @Setter
    @ToString
    public static class Temp {
        private UserRole role;
    }
    
    @GetMapping("temp")
    public ResponseEntity<Temp> temp(Temp temp){
        log.error("{}", temp.getRole());
        
        return new ResponseEntity<>(temp, HttpStatus.OK);
    }
}
