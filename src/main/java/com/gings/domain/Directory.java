package com.gings.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Directory {
    private int id;
    private String name;
    private String company;
    private String job;
    private String field;
    private boolean coworkingChk;
    private String image;
    private List<Introduce> introduce;
}
