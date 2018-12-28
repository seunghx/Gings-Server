package com.gings.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginReq {
    private String email;
    private String pwd;
    private boolean loginKeeped;
}
