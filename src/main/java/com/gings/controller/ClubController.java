package com.gings.controller;

import com.gings.domain.Club;
import com.gings.domain.ClubUser;
import com.gings.model.DefaultRes;
import com.gings.model.Pagination;
import com.gings.security.GingsPrincipal;
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
            log.error("{}", exception);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 클럽 고유번호로 클럽 조회
     * @param clubId
     * @return ResponseEntity
     */
    @GetMapping("clubs/{clubId}")
    public ResponseEntity getClubByClubId(@PathVariable("clubId") final int clubId, final GingsPrincipal principal) throws Throwable{
        try{
            DefaultRes<Club> defaultRes = clubService.findClubByClubId(clubId,principal.getUserId());
            return new ResponseEntity<>(defaultRes,HttpStatus.OK);
        }catch (Exception exception)
        {
            log.error("{}", exception);
            return new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 이벤트 고유번호로 이벤트 조회
     * @param clubId, eventId
     * @return ResponseEntity
     */
    @GetMapping("clubs/{clubId}/{eventId}")
    public ResponseEntity getEventByEventId(@PathVariable("clubId") final int clubId, @PathVariable("eventId") final int eventId, final GingsPrincipal principal){
        try{
            DefaultRes<Event> defaultRes = clubService.findEventByEvent(clubId,eventId,principal.getUserId());
            return new ResponseEntity<>(defaultRes,HttpStatus.OK);
        }catch (Exception exception)
        {
            log.error("{}", exception);
            return new ResponseEntity<>(FAIL_DEFAULT_RES,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 클럽 가입
     * @return ResqponseEntity
     */
    @PostMapping("clubs/{clubId}/join")
    public ResponseEntity joinClub(@PathVariable("clubId") final int clubId, final GingsPrincipal principal) {
        try{
            return new ResponseEntity<>(clubService.joinClub(clubId, principal.getUserId()), HttpStatus.OK);
        } catch (Exception exception){
            log.error("{}", exception);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 이벤트 가입
     * @return ResqponseEntity
     */
    @PostMapping("events/{eventId}/join")
    public ResponseEntity joinEvent(@PathVariable("eventId") final int eventId, final GingsPrincipal principal) {
        try{
            return new ResponseEntity<>(clubService.joinEvent(eventId, principal.getUserId()), HttpStatus.OK);
        } catch (Exception exception){
            log.error("{}", exception);
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}