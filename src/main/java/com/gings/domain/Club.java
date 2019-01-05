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
public class Club {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int clubId;
    private String introImg;
    private String backImg;
    private String title;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Event> event;
    @JsonIgnore
    private List<ClubUser> users;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userStatus;

}
