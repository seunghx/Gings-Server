package com.gings.model;

import com.gings.domain.Introduce;
import com.gings.domain.Signature;
import com.gings.domain.UserKeyword;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SignUpReq {

    private String email;
    private String name;
    private String pwd;
    private String region;
    private String job;
    private String company;
    private String field;
    private String status;
    private String role;
    private boolean coworkingEnabled;

    private Introduce introduce;
    private List<UserKeyword> keywords;
    private List<Signature> signatures;

}
