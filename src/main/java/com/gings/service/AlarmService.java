package com.gings.service;

import com.gings.dao.BoardMapper;
import com.gings.dao.UserMapper;
import com.gings.model.Alarm;
import com.gings.model.DefaultRes;
import com.gings.model.board.HomeBoard;
import com.gings.utils.ResponseMessage;
import com.gings.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;


@Slf4j
@Service
public class AlarmService {

    private final UserMapper userMapper;
    private final BoardMapper boardMapper;

    private ApplicationEventPublisher eventPublisher;

    public AlarmService(UserMapper userMapper, BoardMapper boardMapper) {
        this.userMapper = userMapper;
        this.boardMapper = boardMapper;
    }

    public DefaultRes insertAlarm(final int id, final int writerId, final String sentence, final String location, final int destination) {
        try {
            userMapper.saveAlarm(id, writerId, sentence, location, destination);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.SAVED_ALARM);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes<List<Alarm>>findByUserId(final int id){
        try{
            List<Alarm> alarm = userMapper.findAlarmByUserId(id);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.GET_ALARM, alarm);
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes<HomeBoard.HomeBoardOneRes>findLocation(final int id){
        try{
            String location = userMapper.findLocationByDestinationId(id);
            HomeBoard.HomeBoardOneRes homeBoardOneRes = new HomeBoard.HomeBoardOneRes();
            if(location.equals("board detail"))
                homeBoardOneRes = boardMapper.findBoardByBoardId(id);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.GET_ALARM, homeBoardOneRes);
        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.DB_ERROR);
        }
    }
}
