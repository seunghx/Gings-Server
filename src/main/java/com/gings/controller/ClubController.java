package com.gings.controller;

import com.gings.domain.Club;
import com.gings.domain.ClubUser;
import com.gings.model.DefaultRes;
import com.gings.model.Pagination;
import com.gings.security.Principal;
import com.gings.security.authentication.Authentication;
import com.gings.service.ClubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@Authentication

public class ClubController {

    private final ClubService clubService;

    public ClubController(final ClubService clubService)
    {
        this.clubService= clubService;
    }

    /**
     * 클럽 전체 조회
     * @return ResponseEntity
     */
    @GetMapping("clubs")
    public ResponseEntity getAllClubs()
    {
        try{
            DefaultRes<List<Club>> defaultRes = clubService.findAllClub();
            return new ResponseEntity<>(defaultRes, HttpStatus.OK);
        } catch (Exception exception)
        {
            log.error(exception.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 클럽 고유번호로 클럽 조회
     * @param clubId
     * @return ResponseEntity
     */
    @GetMapping("clubs/{clubId}")
    public ResponseEntity getClubByClubId(@PathVariable("clubId") final int clubId){
        try{
            DefaultRes<Club> defaultRes = clubService.findClubByClubId(clubId);
            return new ResponseEntity<>(defaultRes,HttpStatus.OK);
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 클럽에 가입여부 조회
     * @param clubId
     * @return ResponseEntity
     */
    @GetMapping("clubs/status/{clubId}")
    public ResponseEntity getStatusByClubId(@PathVariable("clubId") final int clubId, final Principal principal){
        System.out.println("컨트롤러");
        try{
            DefaultRes<String> defaultRes = clubService.findStatusByClub(clubId,principal.getUserId());
            return new ResponseEntity<>(defaultRes,HttpStatus.OK);
        } catch (Exception exception)
        {
            log.error(exception.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
