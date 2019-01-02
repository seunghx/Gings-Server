package com.gings.service;

import com.gings.dao.ClubMapper;
import com.gings.domain.Club;
import com.gings.model.DefaultRes;
import com.gings.model.Pagination;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;



@Slf4j
@Service
public class ClubService {
    private static ClubMapper clubMapper;

    /**
     * 생성자 의존성 주입
     *
     * @param clubMapper
     */
    public ClubService(final ClubMapper clubMapper) {
        this.clubMapper = clubMapper;
    }

    /**
     * 클럽 전체 조회
     *
     * @param
     * @return DefaultRes
     */
    public DefaultRes<List<Club>> findAllClub(final Pagination pagination) {
        final List<Club> clubs = clubMapper.findAllClub(pagination);

        log.error("Find Clubs success. club info : {}", clubs);

        if (clubs.isEmpty()) {
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_CLUB);
        }
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_CLUB, clubs);
    }

    /**
     * 클럽 고유 번호로 클럽 조회
     * @param
     * @return DefaultRes
     */
    public DefaultRes<Club> findClubByClubId(final int clubId){
        final Club club = clubMapper.findClubByClubId(clubId);

        log.error("Find Club info success. club info : {}",club);

        if(club == null) {
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_CLUB_INFO);
        }
        return DefaultRes.res(StatusCode.OK,ResponseMessage.READ_CLUB_INFO, club);
    }
}
