package com.gings.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FCM {
    private String firebaseMessage;
    private int boardId;
}
