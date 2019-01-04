package com.gings.service;

import com.gings.dao.ClubMapper;
import com.gings.domain.Club;
import com.gings.domain.ClubUser;
import com.gings.domain.Event;
import com.gings.domain.EventUser;
import com.gings.model.DefaultRes;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


/**
 *
 * @author nuri
 */
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
     * @return DefaultRes
     */
    public DefaultRes<List<Club>> findAllClub() {
        final List<Club> clubs = clubMapper.findAllClub();

        log.error("Find Clubs success. club info : {}", clubs);

        if (clubs.isEmpty()) {
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_CLUB);
        }
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_CLUB, clubs);
    }

    /**
     * 클럽 고유 번호로 클럽 조회
     * @param clubId
     * @return DefaultRes
     */
    public DefaultRes<Club> findClubByClubId(final int clubId,final int userId) throws Throwable{
        final Club club = Optional.ofNullable(clubMapper.findClubByClubId(clubId))
                                  .orElseThrow(() -> {
                                      log.info("Club does not exist for clubId : {}", clubId);

                                      throw new NoSuchClubException("Club does not exist.");
                                  });

        List<ClubUser> clubUsers = club.getUsers();
        String status = clubUsers.stream()
                                 .filter(user -> user.getUserId() == userId)
                                 .findAny()
                                 .map(user -> user.getStatus())
                                 .orElse("가입하기");
        club.setUserStatus(status);

        club.getEvent()
                .stream()
                .forEach(event -> {
                    String eventStatus =  event.getUsers()
                                               .stream()
                                               .filter(user -> user.getUserId() == userId)
                                               .findAny()
                                               .map(user -> user.getStatus())
                                               .orElse("참여하기");

           event.setEventStatus(eventStatus);
        });

        log.error("Find Club info success. club info : {}",club);

        if(club == null) {
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_CLUB_INFO);
        }
        return DefaultRes.res(StatusCode.OK,ResponseMessage.READ_CLUB_INFO, club);
    }

    /**
     * 클럽 가입
     */
    public DefaultRes joinClub(final int clubId, final int userId) {
        try {
            clubMapper.joinClub(clubId,userId,"가입승인중");
            return DefaultRes.res(StatusCode.OK,ResponseMessage.JOIN_CLUB);
        } catch (Exception exception)
        {
            log.error(exception.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR,ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 이벤트 가입
     */
    public DefaultRes joinEvent(final int eventId, final int userId) {
        try {
            clubMapper.joinEvent(eventId, userId,"참여승인중");
            return DefaultRes.res(StatusCode.OK,ResponseMessage.JOIN_EVENT);
        } catch (Exception exception)
        {
            log.error(exception.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR,ResponseMessage.DB_ERROR);
        }
    }

    /**
     * 이벤트 고유 번호로 이벤트 조회
     * @param clubId, eventId
     * @return DefaultRes
     */
    public DefaultRes findEventByEvent(final int clubId, final int eventId, final int userId){
        final Event event = clubMapper.findEventByEvent(clubId,eventId);

        List<EventUser> eventUsers = event.getUsers();

        String status = eventUsers.stream()
                .filter(user -> user.getUserId() == userId)
                .findAny()
                .map(user -> user.getStatus())
                .orElse("참여하기");
        event.setEventStatus(status);

        log.error("Find Club info success. club info : {}",event);

        if(event == null) {
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_EVENT);
        }
        return DefaultRes.res(StatusCode.OK,ResponseMessage.READ_EVENT, event);
    }
    public static class NoSuchClubException extends RuntimeException {
        public NoSuchClubException(String message){
            super(message);
        }
    }

}
