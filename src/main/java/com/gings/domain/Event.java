package com.gings.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Event {

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int eventId;
    private String date;
    private String time;
    private String title;
    private int limit;
    private String place;
    private String eventImg;
    private String detailImg;

    @JsonIgnore
    private List<EventUser> users;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String eventStatus;

}
