package com.gings.controller;

import com.gings.domain.Club;
import com.gings.model.DefaultRes;
import com.gings.model.Pagination;
import com.gings.service.ClubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.gings.model.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@RequestMapping("clubs")
public class ClubController {

    private final ClubService clubService;

    public ClubController(final ClubService clubService)
    {
        this.clubService= clubService;
    }

    /**
     * 클럽 전체 조회
     * @param pagination
     * @return ResponseEntity
     */
    @GetMapping("")
    public ResponseEntity getAllClubs(final Pagination pagination)
    {
        try{
            DefaultRes<List<Club>> defaultRes = clubService.findAllClub(pagination);
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
    @GetMapping("/{clubId}")
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
}
