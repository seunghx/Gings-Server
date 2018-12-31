package com.gings.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Club {
    private int clubId;
    private String introImg;
    private String backImg;

    private List<Event> event;
    private List<UserStatus> status;
}
