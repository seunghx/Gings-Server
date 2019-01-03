package com.gings.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ClubUser {

    @JsonIgnore
    private int userId;
    private String status;

}
