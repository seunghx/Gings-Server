package com.gings.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Signature {

    private int userId;
    private int writerId;
    private String content;
    private LocalDateTime writeTime;
}
