package com.gings.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gings.security.Principal;
import com.gings.security.authentication.Authentication;

import lombok.extern.slf4j.Slf4j;

@Authentication
@RestController
@Slf4j
public class TempController {
    
    @GetMapping("temp2")
    public ResponseEntity<Principal> temp(Principal principal){
        log.error("{}", principal);
        return new ResponseEntity<>(principal, HttpStatus.OK);
    }
}
