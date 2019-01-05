package com.gings.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private int id;
    private String email;
    private String name;
    private String pwd;
    private String region;
    private String job;
    private String company;
    private String field;
    private String status;
    private String role;
    private String image;
    private boolean coworkingEnabled;

    private List <Introduce> introduce;
    private List<UserKeyword> keywords;
    private List<Signature> signatures;

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + ", pwd=" + pwd + ", region=" + region
                + ", job=" + job + ", company=" + company + ", field=" + field + ", status=" + status + ", role=" + role
                + ", image=" + image + ", coworkingEnabled=" + coworkingEnabled + ", introduce=" + introduce
                + ", keywords=" + keywords + ", signatures=" + signatures + "]";
    }

}
