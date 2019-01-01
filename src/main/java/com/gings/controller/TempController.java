package com.gings.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gings.service.MultipartService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TempController {
    
    @Autowired
    private MultipartService service;
    
    @PostMapping("/temp")
    public ResponseEntity<Void> temp(Temp temp){
        
        log.error("{}", service.uploadMultipleFiles(temp.getFile()));
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping("/temp")
    public ResponseEntity<String> temp2(){
        return new ResponseEntity<>("temp", HttpStatus.OK);
    }
}
