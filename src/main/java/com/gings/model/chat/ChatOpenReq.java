package com.gings.model.chat;


import java.util.List;

import com.gings.utils.code.ChatRoomType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatOpenReq {
    private List<Integer> users;
    private ChatRoomType roomType;
}
