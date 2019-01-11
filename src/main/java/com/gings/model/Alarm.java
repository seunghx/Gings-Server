package com.gings.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class Alarm {
    private int userId;
    private String location;
    private int destinationId;

    @Getter
    @Setter
    public class AlarmGuestBoard{
        private String content;
        private LocalDateTime time;
        private GuestModel.GuestModelUser guestModelUser;
    }

    @Getter
    @Setter
    public class AlarmBoard{

    }
}
