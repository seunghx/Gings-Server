package com.gings.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Event {

    private String date;
    private String time;
    private String title;
    private int limit;
    private String place;
    private String eventImg;
    private String detailImg;

}
