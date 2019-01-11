package com.gings.controller;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;
import com.gings.domain.User;
import com.gings.model.*;
import com.gings.model.board.HomeBoard;
import com.gings.model.board.HomeBoard.HomeBoardOneRes;
import com.gings.security.GingsPrincipal;
import com.gings.security.authentication.Authentication;
import com.gings.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@RequestMapping("alarm")
@Authentication
public class AlarmController {
    private final MyPageService myPageService;
    private final BoardService boardService;
    private final AlarmService alarmService;
    private final UserMapper userMapper;
    private final BoardMapper boardMapper;

    public AlarmController(MyPageService myPageService, BoardService boardService, AlarmService alarmService,
                           UserMapper userMapper, BoardMapper boardMapper) {
        this.myPageService = myPageService;
        this.boardService = boardService;
        this.alarmService = alarmService;
        this.boardMapper = boardMapper;
        this.userMapper = userMapper;
    }

    @GetMapping("/main")
    public ResponseEntity getAllAlarm(final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            System.out.println("아이디 출력: "+id);
            DefaultRes<List<Alarm>> defaultRes = alarmService.findByUserId(id);
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/specific")
    public ResponseEntity getSpecificAlarm(@RequestBody final Alarm alarm, final GingsPrincipal principal){
        try{
            final int id = principal.getUserId();
            DefaultRes<HomeBoard.HomeBoardOneRes>defaultRes = alarmService.findLocation(alarm.getDestinationId());
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        }catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


