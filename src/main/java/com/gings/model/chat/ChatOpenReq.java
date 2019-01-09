package com.gings.model.chat;


import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.gings.utils.code.ChatRoomType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatOpenReq {
    
    @NotEmpty
    private List<Integer> users;
    private ChatRoomType roomType;
}
