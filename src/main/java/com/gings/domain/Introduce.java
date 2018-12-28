package com.gings.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Introduce {

    private int id;
    private int userId;
    private String content;
    private List<String> imgs = new ArrayList<>();

}
