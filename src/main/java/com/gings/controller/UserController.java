package com.gings.controller;

import com.gings.model.SignUpReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;
import static com.gings.utils.ResponseMessage.CREATED_USER;
import static com.gings.utils.ResponseMessage.NOT_FOUND_USER;
import static com.gings.utils.ResponseMessage.READ_USER;

@Slf4j
@RestController
@RequestMapping("users")
public class UserController {

    // 회원 조회

    @GetMapping("")
    public ResponseEntity getUser() {
        try {
            return new ResponseEntity<>(READ_USER, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(NOT_FOUND_USER, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 회원 등록

    @PostMapping("")
    public ResponseEntity signUp(final SignUpReq signUpReq){
        try {
            return new ResponseEntity<>(CREATED_USER, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
